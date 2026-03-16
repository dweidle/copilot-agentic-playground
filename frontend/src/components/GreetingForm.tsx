import { useState, type FormEvent } from 'react'

interface GreetingResponse {
  message: string
}

export function GreetingForm() {
  const [name, setName] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [shaking, setShaking] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)

    try {
      const params = name.trim() ? `?name=${encodeURIComponent(name.trim())}` : ''
      const res = await fetch(`/api/greeting${params}`)

      if (!res.ok) {
        const problem = await res.json().catch(() => null)
        throw new Error(problem?.detail ?? `Server error: ${res.status}`)
      }

      const data: GreetingResponse = await res.json()
      setMessage(data.message)
      setShaking(true)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unexpected error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={`greeting-form${shaking ? ' shake' : ''}`} onAnimationEnd={() => setShaking(false)}>
      <h1>Greeting Service</h1>
      <form onSubmit={handleSubmit}>
        <div className="input-row">
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter your name (optional)"
            aria-label="Name"
            disabled={loading}
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Loading…' : 'Greet'}
          </button>
        </div>
      </form>

      {message && (
        <div className="result success" role="status">
          {message}
        </div>
      )}

      {error && (
        <div className="result error" role="alert">
          {error}
        </div>
      )}
    </div>
  )
}
