// Authentication utility functions for JWT token management

export interface AuthUser {
  email: string;
  token: string;
}

// Store token in localStorage
export const setAuth = (token: string, email: string): void => {
  if (typeof window !== "undefined") {
    localStorage.setItem("token", token);
    localStorage.setItem("userEmail", email);
  }
};

// Get token from localStorage
export const getToken = (): string | null => {
  if (typeof window !== "undefined") {
    return localStorage.getItem("token");
  }
  return null;
};

// Get user email from localStorage
export const getUserEmail = (): string | null => {
  if (typeof window !== "undefined") {
    return localStorage.getItem("userEmail");
  }
  return null;
};

// Get full auth data
export const getAuthUser = (): AuthUser | null => {
  const token = getToken();
  const email = getUserEmail();

  if (token && email) {
    return { token, email };
  }
  return null;
};

// Check if user is authenticated
export const isAuthenticated = (): boolean => {
  return !!getToken();
};

// Clear auth data (logout)
export const clearAuth = (): void => {
  if (typeof window !== "undefined") {
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
  }
};

// Make authenticated API request
export const fetchWithAuth = async (
  url: string,
  options: RequestInit = {}
): Promise<Response> => {
  const token = getToken();

  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  };

  return fetch(url, {
    ...options,
    headers,
  });
};
