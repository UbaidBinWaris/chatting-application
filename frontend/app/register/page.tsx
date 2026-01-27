"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import API_ENDPOINTS from "@/lib/api-config";

export default function RegisterPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess(false);

    // Validate passwords match
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      setLoading(false);
      return;
    }

    // Validate password length
    if (password.length < 6) {
      setError("Password must be at least 6 characters long");
      setLoading(false);
      return;
    }

    try {
      const res = await fetch(API_ENDPOINTS.auth.register, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Registration failed");
      }

      await res.text();
      setSuccess(true);

      // Redirect to login after 2 seconds
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background text-foreground">
      <form
        onSubmit={handleRegister}
        className="bg-zinc-900 p-8 rounded-xl w-[380px]"
      >
        <h1 className="text-2xl font-bold mb-6">Create Account</h1>

        {error && (
          <p className="bg-red-600 p-2 rounded mb-4 text-sm">{error}</p>
        )}

        {success && (
          <p className="bg-green-600 p-2 rounded mb-4 text-sm">
            Registration successful! Redirecting to login...
          </p>
        )}

        <input
          type="email"
          placeholder="Email"
          className="w-full p-3 rounded bg-zinc-800 mb-4 outline-none"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
          suppressHydrationWarning
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full p-3 rounded bg-zinc-800 mb-4 outline-none"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={6}
          autoComplete="new-password"
          suppressHydrationWarning
        />

        <input
          type="password"
          placeholder="Confirm Password"
          className="w-full p-3 rounded bg-zinc-800 mb-4 outline-none"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
          minLength={6}
          autoComplete="new-password"
          suppressHydrationWarning
        />

        <button
          disabled={loading || success}
          className="w-full p-3 rounded bg-blue-600 hover:bg-blue-700 transition font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? "Creating Account..." : "Sign Up"}
        </button>

        <p className="mt-4 text-center text-sm text-zinc-400">
          Already have an account?{" "}
          <Link href="/login" className="text-blue-500 hover:underline">
            Login here
          </Link>
        </p>
      </form>
    </div>
  );
}
