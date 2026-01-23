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
          <div className="bg-zinc-800 p-4 rounded-lg">
            <p className="text-sm text-zinc-300">
              Your authentication is managed with JWT tokens. Your session is secure and protected.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}
