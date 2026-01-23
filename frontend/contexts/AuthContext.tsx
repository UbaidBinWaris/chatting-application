"use client";

import { createContext, useContext, useState, useMemo } from "react";
import { useRouter } from "next/navigation";
import { getAuthUser, setAuth, clearAuth, AuthUser } from "@/lib/auth";

interface AuthContextType {
  user: AuthUser | null;
  login: (token: string, email: string) => void;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: Readonly<{ children: React.ReactNode }>) {
  const [user, setUser] = useState<AuthUser | null>(() => getAuthUser());
  const [isLoading] = useState(false);
  const router = useRouter();

  const login = (token: string, email: string) => {
    setAuth(token, email);
    setUser({ token, email });
  };

  const logout = () => {
    clearAuth();
    setUser(null);
    router.push("/login");
  };

  const value = useMemo(
    () => ({ user, login, logout, isLoading }),
    [user, isLoading]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
