import { useState, type FormEvent } from 'react'

interface GreetingResponse {
  message: string
  language: string
  flag: string
}

export function GreetingForm() {
  const [name, setName] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [flag, setFlag] = useState<string | null>(null)
  const [language, setLanguage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [shaking, setShaking] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    setFlag(null)
    setLanguage(null)

    try {
      const params = name.trim() ? `?name=${encodeURIComponent(name.trim())}` : ''
      const res = await fetch(`/api/greeting${params}`)

      if (!res.ok) {
        const problem = await res.json().catch(() => null)
        throw new Error(problem?.detail ?? `Server error: ${res.status}`)
      }

      const data: GreetingResponse = await res.json()
      setMessage(data.message)
      setFlag(data.flag)
      setLanguage(data.language)
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
          <span className="flag-display" aria-label={language ?? undefined}>
            {flag === 'bw' ? (
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Coat_of_arms_of_Baden-W%C3%BCrttemberg.svg/240px-Coat_of_arms_of_Baden-W%C3%BCrttemberg.svg.png"
                alt="Baden-Württemberg"
                className="bw-wappen"
              />
            ) : (
              <span role="img" aria-label={language ?? undefined}>{flag}</span>
            )}
          </span>
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
