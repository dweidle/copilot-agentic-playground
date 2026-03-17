import { render, screen, waitFor, fireEvent, act } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { GreetingForm } from './GreetingForm'

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/** Build a minimal Response-like mock that fetch returns. */
function mockFetchOk(body: object) {
  return vi.fn().mockResolvedValue({
    ok: true,
    status: 200,
    json: () => Promise.resolve(body),
  })
}

function mockFetchError(status: number, body: object) {
  return vi.fn().mockResolvedValue({
    ok: false,
    status,
    json: () => Promise.resolve(body),
  })
}

function mockFetchNetworkFailure(message: string) {
  return vi.fn().mockRejectedValue(new Error(message))
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('GreetingForm', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  // -------------------------------------------------------------------------
  // 1. Renders correctly
  // -------------------------------------------------------------------------
  it('should render heading, input, and button with no result initially', () => {
    // ARRANGE & ACT
    render(<GreetingForm />)

    // ASSERT — structural elements are present
    expect(screen.getByRole('heading', { name: /greeting service/i })).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: /name/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /greet/i })).toBeInTheDocument()

    // ASSERT — no result shown before any submission
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
  })

  // -------------------------------------------------------------------------
  // 2. Shows greeting for a named user
  // -------------------------------------------------------------------------
  it('should show the greeting message when a name is submitted', async () => {
    // ARRANGE
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchOk({ message: 'Hello, Daniel! 👋' }))
    render(<GreetingForm />)

    // ACT
    await user.type(screen.getByRole('textbox', { name: /name/i }), 'Daniel')
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT
    const result = await screen.findByRole('status')
    expect(result).toHaveTextContent('Hello, Daniel! 👋')
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
  })

  // -------------------------------------------------------------------------
  // 3. Shows default greeting when name is empty
  // -------------------------------------------------------------------------
  it('should call fetch without a name param and show default greeting when input is empty', async () => {
    // ARRANGE
    const user = userEvent.setup()
    const fetchMock = mockFetchOk({ message: 'Hello, World!' })
    vi.stubGlobal('fetch', fetchMock)
    render(<GreetingForm />)

    // ACT — leave input blank and submit
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT — URL must NOT contain a ?name= query string
    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledOnce()
    })
    const [calledUrl] = fetchMock.mock.calls[0] as [string]
    expect(calledUrl).toBe('/api/greeting')
    expect(calledUrl).not.toContain('?name=')

    const result = await screen.findByRole('status')
    expect(result).toHaveTextContent('Hello, World!')
  })

  // -------------------------------------------------------------------------
  // 4. Shows loading state while fetch is in-flight
  // -------------------------------------------------------------------------
  it('should disable input and show "Loading…" while the request is pending', async () => {
    // ARRANGE — a fetch that never resolves
    const user = userEvent.setup()
    vi.stubGlobal(
      'fetch',
      vi.fn(() => new Promise(() => { /* intentionally never resolves */ })),
    )
    render(<GreetingForm />)

    // ACT — fire the click but do NOT await it
    void user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT — loading indicators appear immediately after the click starts
    await waitFor(() => {
      expect(screen.getByRole('button')).toHaveTextContent('Loading…')
      expect(screen.getByRole('button')).toBeDisabled()
      expect(screen.getByRole('textbox', { name: /name/i })).toBeDisabled()
    })
  })

  // -------------------------------------------------------------------------
  // 5. Shows error message on HTTP error response
  // -------------------------------------------------------------------------
  it('should show the detail message when the server responds with an HTTP error', async () => {
    // ARRANGE
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchError(400, { detail: 'Bad Request' }))
    render(<GreetingForm />)

    // ACT
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT — wait for all state updates (setError + setLoading) to flush
    const alert = await screen.findByRole('alert')
    await waitFor(() => expect(screen.getByRole('button')).not.toBeDisabled())
    expect(alert).toHaveTextContent('Bad Request')
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  // -------------------------------------------------------------------------
  // 6. Shows fallback error on network failure
  // -------------------------------------------------------------------------
  it('should show the network error message when fetch rejects', async () => {
    // ARRANGE
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchNetworkFailure('Network error'))
    render(<GreetingForm />)

    // ACT
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT
    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent('Network error')
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  // -------------------------------------------------------------------------
  // 7. Applies shake class after a successful greeting
  // -------------------------------------------------------------------------
  it('should apply the shake class to the greeting-form div after a successful greeting', async () => {
    // ARRANGE
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchOk({ message: 'Hello, Daniel! 👋' }))
    render(<GreetingForm />)

    // ACT
    await user.type(screen.getByRole('textbox', { name: /name/i }), 'Daniel')
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // ASSERT — shake class is added once the greeting arrives
    const form = await screen.findByRole('status')
    const container = form.closest('.greeting-form')
    await waitFor(() => {
      expect(container).toHaveClass('shake')
    })
  })

  // -------------------------------------------------------------------------
  // 8. Removes shake class after onAnimationEnd fires
  // -------------------------------------------------------------------------
  it('should remove the shake class after the animation ends', async () => {
    // ARRANGE
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchOk({ message: 'Hello, Daniel! 👋' }))
    const { container } = render(<GreetingForm />)

    // ACT
    await user.type(screen.getByRole('textbox', { name: /name/i }), 'Daniel')
    await user.click(screen.getByRole('button', { name: /greet/i }))

    // Wait for the shake class to appear first
    const greetingDiv = container.querySelector('.greeting-form')!
    await waitFor(() => expect(greetingDiv).toHaveClass('shake'))

    // Simulate the animation ending
    await act(async () => {
      fireEvent.animationEnd(greetingDiv, { bubbles: true })
    })

    // ASSERT — shake class is removed after the animation ends
    await waitFor(() => expect(greetingDiv).not.toHaveClass('shake'))
  })

  // -------------------------------------------------------------------------
  // 9. Displays flag emoji for a standard language
  // -------------------------------------------------------------------------
  it('should display the flag emoji for a standard language', async () => {
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchOk({ message: 'Hallo, Daniel! 👋', language: 'Deutsch', flag: '🇩🇪' }))
    render(<GreetingForm />)
    await user.type(screen.getByRole('textbox', { name: /name/i }), 'Daniel')
    await user.click(screen.getByRole('button', { name: /greet/i }))
    await screen.findByRole('status')
    expect(screen.getByRole('img', { name: 'Deutsch' })).toBeInTheDocument()
  })

  // -------------------------------------------------------------------------
  // 10. Displays BW Wappen image for Schwäbisch
  // -------------------------------------------------------------------------
  it('should display BW Wappen image for Schwäbisch', async () => {
    const user = userEvent.setup()
    vi.stubGlobal('fetch', mockFetchOk({ message: 'Grüaß di, Daniel! 👋', language: 'Schwäbisch', flag: 'bw' }))
    render(<GreetingForm />)
    await user.type(screen.getByRole('textbox', { name: /name/i }), 'Daniel')
    await user.click(screen.getByRole('button', { name: /greet/i }))
    await screen.findByRole('status')
    const img = screen.getByAltText('Baden-Württemberg')
    expect(img).toBeInTheDocument()
    expect(img).toHaveAttribute('src', expect.stringContaining('Baden-W'))
  })
})
