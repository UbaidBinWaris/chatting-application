import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'Chatting Application',
  description: 'Real-time chatting application with Next.js and Spring Boot',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  )
}
