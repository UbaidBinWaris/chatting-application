"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAuthUser, clearAuth } from "@/lib/auth";

export default function Dashboard() {
  const router = useRouter();
  const authUser = getAuthUser();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    if (!authUser) {
      router.push("/login");
    }
  }, [authUser, router]);

  const handleLogout = () => {
    clearAuth();
    router.push("/login");
  };

  if (!mounted || !authUser) {
    return (
      <div className="min-h-screen bg-black text-white flex items-center justify-center">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-black text-white">
      {/* Header with logout */}
      <header className="bg-zinc-900 border-b border-zinc-800">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold">Chatting App</h1>
          <div className="flex items-center gap-4">
            <span className="text-zinc-400 text-sm">{authUser.email}</span>
            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-red-600 hover:bg-red-700 rounded transition font-semibold text-sm"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="bg-zinc-900 rounded-xl p-8">
          <h2 className="text-3xl font-bold mb-4">Welcome to Dashboard! 🎉</h2>
          <p className="text-zinc-400 mb-4">
            You are successfully logged in as <span className="text-blue-500">{authUser.email}</span>
          </p>
          <div className="bg-zinc-800 p-4 rounded-lg mb-6">
            <p className="text-sm text-zinc-300">
              Your authentication is managed with JWT tokens. Your session is secure and protected.
            </p>
          </div>

          {/* Chat Navigation */}
          <div className="flex flex-col gap-4">
            <h3 className="text-xl font-semibold">Quick Actions</h3>
            <button
              onClick={() => router.push('/chat')}
              className="px-6 py-4 bg-blue-600 hover:bg-blue-700 rounded-lg transition font-semibold text-lg flex items-center justify-center gap-2"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              Go to Chat
            </button>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
              <div className="bg-zinc-800 p-4 rounded-lg">
                <h4 className="font-semibold mb-2">💬 Direct Messages</h4>
                <p className="text-sm text-zinc-400">Chat one-on-one with other users</p>
              </div>
              <div className="bg-zinc-800 p-4 rounded-lg">
                <h4 className="font-semibold mb-2">👥 Group Chats</h4>
                <p className="text-sm text-zinc-400">Create and manage group conversations</p>
              </div>
              <div className="bg-zinc-800 p-4 rounded-lg">
                <h4 className="font-semibold mb-2">⚡ Real-time</h4>
                <p className="text-sm text-zinc-400">Instant message delivery via WebSocket</p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
