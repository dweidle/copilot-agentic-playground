#!/usr/bin/env bash
# Generates release-notes.md by consolidating:
#   - Closed GitHub issues since the previous release tag
#   - Git commits between the previous tag and the current tag
#   - Maven Surefire test results from target/surefire-reports/
#
# Required environment variables:
#   GH_TOKEN   - GitHub token with repo read access
#   REPO       - owner/repo slug (e.g. "dweidle/copilot-agentic-playground")
#   TAG        - current release tag (e.g. "v1.2.3")
#
# Output: release-notes.md in the current working directory

set -euo pipefail

REPO="${REPO:?REPO must be set}"
TAG="${TAG:?TAG must be set}"
GH_TOKEN="${GH_TOKEN:?GH_TOKEN must be set}"
OUTPUT="${OUTPUT:-release-notes.md}"
SUREFIRE_DIR="${SUREFIRE_DIR:-target/surefire-reports}"

API="https://api.github.com/repos/${REPO}"
AUTH_HEADER="Authorization: Bearer ${GH_TOKEN}"
ACCEPT_HEADER="Accept: application/vnd.github+json"

# ---------------------------------------------------------------------------
# Determine previous tag and its date
# ---------------------------------------------------------------------------
PREV_TAG=$(git tag --sort=-version:refname | grep -v "^${TAG}$" | head -1 || true)
if [[ -n "${PREV_TAG}" ]]; then
  PREV_DATE=$(git log -1 --format="%cI" "${PREV_TAG}")
  COMMIT_RANGE="${PREV_TAG}..${TAG}"
else
  # First release — include everything
  PREV_DATE="1970-01-01T00:00:00Z"
  COMMIT_RANGE="${TAG}"
fi

RELEASE_DATE=$(date -u +"%Y-%m-%d")

# ---------------------------------------------------------------------------
# Fetch closed issues since the previous release
# ---------------------------------------------------------------------------
fetch_issues() {
  local page=1
  local per_page=100
  local all_issues=""
  while true; do
    local response
    response=$(curl -sf \
      -H "${AUTH_HEADER}" \
      -H "${ACCEPT_HEADER}" \
      "${API}/issues?state=closed&per_page=${per_page}&page=${page}&since=${PREV_DATE}&sort=updated&direction=asc")
    local count
    count=$(echo "${response}" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len([i for i in d if 'pull_request' not in i]))" 2>/dev/null || echo 0)
    # Filter out pull requests and append
    local filtered
    filtered=$(echo "${response}" | python3 -c "
import sys, json
data = json.load(sys.stdin)
issues = [i for i in data if 'pull_request' not in i]
print(json.dumps(issues))
")
    all_issues=$(echo "${all_issues}${filtered}" | python3 -c "
import sys, json
chunks = sys.stdin.read()
# Each chunk is a JSON array; merge them
import re
arrays = re.findall(r'\[.*?\]', chunks, re.DOTALL)
merged = []
for a in arrays:
    try:
        merged.extend(json.loads(a))
    except Exception:
        pass
print(json.dumps(merged))
")
    if [[ "${count}" -lt "${per_page}" ]]; then
      break
    fi
    page=$((page + 1))
  done
  echo "${all_issues}"
}

ISSUES_JSON=$(fetch_issues)

# ---------------------------------------------------------------------------
# Parse test results from Surefire XML reports
# ---------------------------------------------------------------------------
parse_tests() {
  if [[ ! -d "${SUREFIRE_DIR}" ]]; then
    echo "0 0 0 0 0.0"
    return
  fi
  python3 - "${SUREFIRE_DIR}" <<'PYEOF'
import sys, os, glob
from xml.etree import ElementTree as ET

surefire_dir = sys.argv[1]
total = failed = skipped = 0
duration = 0.0

for path in glob.glob(os.path.join(surefire_dir, 'TEST-*.xml')):
    try:
        root = ET.parse(path).getroot()
        total    += int(root.get('tests',    0))
        failed   += int(root.get('failures', 0)) + int(root.get('errors', 0))
        skipped  += int(root.get('skipped',  0))
        duration += float(root.get('time',   0))
    except Exception:
        pass

passed = total - failed - skipped
print(total, passed, failed, skipped, f"{duration:.1f}")
PYEOF
}

read -r T_TOTAL T_PASSED T_FAILED T_SKIPPED T_DURATION <<< "$(parse_tests)"

# ---------------------------------------------------------------------------
# Build Markdown document
# ---------------------------------------------------------------------------
{
  echo "# Release Notes — ${TAG}"
  echo ""
  echo "**Released:** ${RELEASE_DATE}"
  if [[ -n "${PREV_TAG}" ]]; then
    echo "**Previous release:** ${PREV_TAG}"
  fi
  echo ""

  # ----- Closed Issues -----
  echo "## Closed Issues"
  echo ""
  ISSUE_COUNT=$(echo "${ISSUES_JSON}" | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo 0)
  if [[ "${ISSUE_COUNT}" -eq 0 ]]; then
    echo "_No issues were closed in this release._"
  else
    echo "| # | Title | Labels |"
    echo "|---|-------|--------|"
    echo "${ISSUES_JSON}" | python3 -c "
import sys, json
issues = json.load(sys.stdin)
for i in sorted(issues, key=lambda x: x['number']):
    num    = i['number']
    title  = i['title'].replace('|', '\\\\|')
    url    = i['html_url']
    labels = ', '.join(l['name'] for l in i.get('labels', []))
    print(f'| [#{num}]({url}) | {title} | {labels} |')
"
  fi
  echo ""

  # ----- Commits -----
  echo "## Commits"
  echo ""
  COMMIT_LOG=$(git --no-pager log "${COMMIT_RANGE}" --pretty=format:'%h|||%s|||%an|||%as' --no-merges 2>/dev/null || true)
  if [[ -z "${COMMIT_LOG}" ]]; then
    echo "_No commits in this release._"
  else
    echo "| SHA | Message | Author | Date |"
    echo "|-----|---------|--------|------|"
    while IFS='|||' read -r sha msg author date; do
      msg_escaped="${msg//|/\\|}"
      echo "| \`${sha}\` | ${msg_escaped} | ${author} | ${date} |"
    done <<< "${COMMIT_LOG}"
  fi
  echo ""

  # ----- Test Results -----
  echo "## Test Results"
  echo ""
  if [[ "${T_TOTAL}" -eq 0 ]]; then
    echo "_No test results available._"
  else
    echo "| Metric | Value |"
    echo "|--------|-------|"
    echo "| Total  | ${T_TOTAL} |"
    echo "| ✅ Passed | ${T_PASSED} |"
    echo "| ❌ Failed | ${T_FAILED} |"
    echo "| ⏭️ Skipped | ${T_SKIPPED} |"
    echo "| ⏱️ Duration | ${T_DURATION}s |"
  fi
  echo ""

} > "${OUTPUT}"

echo "Release notes written to ${OUTPUT}"
