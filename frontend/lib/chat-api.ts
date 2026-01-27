// API functions for chat operations
import { apiRequest } from './auth';
import { Conversation, Message } from './websocket';

const API_URL = 'http://localhost:8080/api';

export interface CreateGroupRequest {
  name: string;
  participantIds: number[];
}

export interface User {
  id: number;
  email: string;
  role: string;
}

export const chatApi = {
  // Get all conversations for the current user
  getConversations: async (): Promise<Conversation[]> => {
    const response = await apiRequest(`${API_URL}/chat/conversations`);
    return response;
  },

  // Create a direct conversation with another user
  createDirectConversation: async (otherUserId: number): Promise<Conversation> => {
    const response = await apiRequest(
      `${API_URL}/chat/conversations/direct?otherUserId=${otherUserId}`,
      { method: 'POST' }
    );
    return response;
  },

  // Create a group conversation
  createGroupConversation: async (request: CreateGroupRequest): Promise<Conversation> => {
    const response = await apiRequest(`${API_URL}/chat/conversations/group`, {
      method: 'POST',
      body: JSON.stringify(request),
    });
    return response;
  },

  // Get messages for a conversation
  getMessages: async (
    conversationId: number,
    page: number = 0,
    size: number = 50
  ): Promise<Message[]> => {
    const response = await apiRequest(
      `${API_URL}/chat/conversations/${conversationId}/messages?page=${page}&size=${size}`
    );
    return response;
  },

  // Send a message (also can use WebSocket)
  sendMessage: async (
    conversationId: number,
    content: string,
    messageType: string = 'TEXT'
  ): Promise<Message> => {
    const response = await apiRequest(`${API_URL}/chat/messages`, {
      method: 'POST',
      body: JSON.stringify({ conversationId, content, messageType }),
    });
    return response;
  },

  // Add a participant to a group
  addParticipant: async (conversationId: number, userId: number): Promise<void> => {
    await apiRequest(
      `${API_URL}/chat/conversations/${conversationId}/participants?userId=${userId}`,
      { method: 'POST' }
    );
  },

  // Remove a participant from a group
  removeParticipant: async (conversationId: number, userId: number): Promise<void> => {
    await apiRequest(
      `${API_URL}/chat/conversations/${conversationId}/participants/${userId}`,
      { method: 'DELETE' }
    );
  },

  // Make a participant an admin
  makeAdmin: async (conversationId: number, userId: number): Promise<void> => {
    await apiRequest(
      `${API_URL}/chat/conversations/${conversationId}/participants/${userId}/admin`,
      { method: 'PUT' }
    );
  },

  // Search for users
  searchUsers: async (query: string): Promise<User[]> => {
    const response = await apiRequest(`${API_URL}/users/search?query=${encodeURIComponent(query)}`);
    return response;
  },

  // Get current user
  getCurrentUser: async (): Promise<User> => {
    const response = await apiRequest(`${API_URL}/users/me`);
    return response;
  },
};
