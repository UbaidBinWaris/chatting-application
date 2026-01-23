// API configuration and endpoints

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const API_ENDPOINTS = {
  auth: {
    register: `${API_BASE_URL}/api/auth/register`,
    login: `${API_BASE_URL}/api/auth/login`,
  },
  // Add more endpoints as you build features
  // chat: {
  //   messages: `${API_BASE_URL}/api/chat/messages`,
  //   rooms: `${API_BASE_URL}/api/chat/rooms`,
  // },
};

export default API_ENDPOINTS;
