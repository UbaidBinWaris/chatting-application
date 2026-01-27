// WebSocket service for real-time messaging
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface Message {
  id: number;
  conversationId: number;
  senderId: number;
  senderEmail: string;
  content: string;
  messageType: string;
  createdAt: string;
  isRead: boolean;
}

export interface Conversation {
  id: number;
  name: string | null;
  isGroup: boolean;
  participants: Participant[];
  lastMessage?: Message;
  unreadCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Participant {
  id: number;
  userId: number;
  userEmail: string;
  isAdmin: boolean;
  joinedAt: string;
}

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, any> = new Map();

  connect(token: string, onConnect?: () => void) {
    if (this.client?.active) {
      console.log('Already connected');
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      onConnect: () => {
        console.log('WebSocket Connected');
        onConnect?.();
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      },
      reconnectDelay: 5000,
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.subscriptions.forEach((sub) => sub.unsubscribe());
      this.subscriptions.clear();
      this.client.deactivate();
      this.client = null;
    }
  }

  subscribeToConversation(
    conversationId: number,
    onMessage: (message: Message) => void
  ) {
    if (!this.client?.active) {
      console.error('WebSocket not connected');
      return;
    }

    const topic = `/topic/conversation.${conversationId}`;
    const subscription = this.client.subscribe(topic, (message) => {
      const parsedMessage = JSON.parse(message.body);
      onMessage(parsedMessage);
    });

    this.subscriptions.set(topic, subscription);
  }

  unsubscribeFromConversation(conversationId: number) {
    const topic = `/topic/conversation.${conversationId}`;
    const subscription = this.subscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(topic);
    }
  }

  sendMessage(conversationId: number, content: string, messageType: string = 'TEXT') {
    if (!this.client?.active) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({
        conversationId,
        content,
        messageType,
      }),
    });
  }

  sendTypingIndicator(conversationId: number, isTyping: boolean) {
    if (!this.client?.active) {
      return;
    }

    this.client.publish({
      destination: '/app/chat.typing',
      body: JSON.stringify({
        conversationId,
        isTyping,
      }),
    });
  }
}

export const websocketService = new WebSocketService();
