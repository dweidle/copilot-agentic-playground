#!/usr/bin/env bash
# Opens a Pull Request for a GitHub issue and posts an implementation summary
# comment back on the issue.
#
# Usage:
#   bash scripts/open-issue-pr.sh <issue-number> <pr-title> <pr-body>
#
# Requirements:
#   - gh CLI installed and authenticated (gh auth login)
#   - Current branch is the feature branch to use as PR head
#
# Example:
#   bash scripts/open-issue-pr.sh 9 \
#     "feat: automate release note generation" \
#     "Adds scripts/generate-release-notes.sh and updates the release workflow."

set -euo pipefail

ISSUE_NUMBER="${1:?Usage: $0 <issue-number> <pr-title> <pr-body>}"
PR_TITLE="${2:?Usage: $0 <issue-number> <pr-title> <pr-body>}"
PR_BODY="${3:?Usage: $0 <issue-number> <pr-title> <pr-body>}"

BRANCH=$(git rev-parse --abbrev-ref HEAD)
REPO=$(git remote get-url origin | sed 's|.*github.com[:/]\(.*\)\.git|\1|')

# ---------------------------------------------------------------------------
# Open the Pull Request
# ---------------------------------------------------------------------------
PR_FULL_BODY="${PR_BODY}

Closes #${ISSUE_NUMBER}"

PR_URL=$(gh pr create \
  --repo "${REPO}" \
  --head "${BRANCH}" \
  --base main \
  --title "${PR_TITLE}" \
  --body "${PR_FULL_BODY}")

echo "✅ Pull Request opened: ${PR_URL}"

# ---------------------------------------------------------------------------
# Post a summary comment on the issue
# ---------------------------------------------------------------------------
COMMENT="🤖 **Implementation complete** — see ${PR_URL}

### What was done

${PR_BODY}

### Branch
\`${BRANCH}\`

### Commits
$(git --no-pager log origin/main..HEAD --oneline | sed 's/^/- /')"

gh issue comment "${ISSUE_NUMBER}" \
  --repo "${REPO}" \
  --body "${COMMENT}"

echo "✅ Summary comment posted on issue #${ISSUE_NUMBER}"
