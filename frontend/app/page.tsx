"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { isAuthenticated } from "@/lib/auth";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    // Redirect to dashboard if already logged in
    if (isAuthenticated()) {
      router.push("/dashboard");
    }
  }, [router]);

  return (
    <div className="min-h-screen bg-black text-white flex items-center justify-center">
      <div className="max-w-2xl text-center px-4">
        <h1 className="text-5xl font-bold mb-6 bg-gradient-to-r from-blue-500 to-purple-600 bg-clip-text text-transparent">
          Welcome to Chatting App
        </h1>
        <p className="text-xl text-zinc-400 mb-8">
          Connect with friends and colleagues in real-time with secure JWT authentication
        </p>

        <div className="flex gap-4 justify-center">
          <Link
            href="/login"
            className="px-8 py-3 bg-blue-600 hover:bg-blue-700 rounded-lg font-semibold transition"
          >
            Login
          </Link>
          <Link
            href="/register"
            className="px-8 py-3 bg-zinc-800 hover:bg-zinc-700 rounded-lg font-semibold transition"
          >
            Sign Up
          </Link>
        </div>

        <div className="mt-12 p-6 bg-zinc-900 rounded-lg">
          <h2 className="text-lg font-semibold mb-3">Features:</h2>
          <ul className="text-sm text-zinc-400 space-y-2">
            <li>✅ Secure JWT Authentication</li>
            <li>✅ Real-time Messaging</li>
            <li>✅ Modern UI with Next.js</li>
            <li>✅ Spring Boot Backend</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
