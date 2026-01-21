'use client'

import { useState, FormEvent } from 'react'
import { useRouter } from 'next/navigation'
import axios from 'axios'

export default function Page() {
  const router = useRouter()
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    // Client-side validation
    if (!formData.username || !formData.password) {
      setError('Username and password are required')
      setLoading(false)
      return
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters')
      setLoading(false)
      return
    }

    try {
      // Send login request to backend
      const response = await axios.post(
        `${process.env.NEXT_PUBLIC_API_URL}/auth/login`,
        formData,
        {
          headers: {
            'Content-Type': 'application/json'
          },
          withCredentials: true // Include cookies for session management
        }
      )

      if (response.data.token) {
        // Store token securely
        localStorage.setItem('authToken', response.data.token)
        
        // Redirect to dashboard or home
        router.push('/dashboard')
      }
    } catch (err: any) {
      setError(
        err.response?.data?.message || 
        'Login failed. Please check your credentials.'
      )
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  return (
    <main>
      <h1>Login</h1>
      
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username or Email</label>
          <input
            type="text"
            id="username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
            autoComplete="username"
            disabled={loading}
          />
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            minLength={6}
            autoComplete="current-password"
            disabled={loading}
          />
        </div>

        {error && (
          <div role="alert">
            {error}
          </div>
        )}

        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>

      <div>
        <p>Don't have an account? <a href="/register">Register</a></p>
      </div>
    </main>
  )
}
