'use client'

import { useState, useEffect } from 'react'
import axios from 'axios'

export default function Home() {
  const [message, setMessage] = useState('')
  const [messages, setMessages] = useState<any[]>([])
  const [connectionStatus, setConnectionStatus] = useState('Checking...')

  useEffect(() => {
    checkBackendConnection()
    fetchMessages()
  }, [])

  const checkBackendConnection = async () => {
    try {
      const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/health`)
      setConnectionStatus('Connected')
    } catch (error) {
      setConnectionStatus('Disconnected')
    }
  }

  const fetchMessages = async () => {
    try {
      const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/messages`)
      setMessages(response.data)
    } catch (error) {
      console.error('Error fetching messages:', error)
    }
  }

  const sendMessage = async () => {
    if (!message.trim()) return
    
    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/messages`, {
        content: message,
        sender: 'User'
      })
      setMessage('')
      fetchMessages()
    } catch (error) {
      console.error('Error sending message:', error)
    }
  }

  return (
    <main style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Chatting Application</h1>
      <p>Backend Status: <strong>{connectionStatus}</strong></p>
      
      <div style={{ marginTop: '20px', marginBottom: '20px' }}>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
          placeholder="Type a message..."
          style={{ padding: '10px', width: '70%', marginRight: '10px' }}
        />
        <button onClick={sendMessage} style={{ padding: '10px 20px' }}>
          Send
        </button>
      </div>

      <div style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '5px' }}>
        <h2>Messages</h2>
        {messages.length === 0 ? (
          <p>No messages yet</p>
        ) : (
          messages.map((msg, index) => (
            <div key={index} style={{ padding: '10px', borderBottom: '1px solid #eee' }}>
              <strong>{msg.sender}:</strong> {msg.content}
            </div>
          ))
        )}
      </div>
    </main>
  )
}
