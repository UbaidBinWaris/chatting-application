'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getToken, getUserEmail } from '@/lib/auth';
import { chatApi } from '@/lib/chat-api';
import { websocketService, Conversation, Message } from '@/lib/websocket';
import ConversationList from '@/components/ConversationList';
import ChatWindow from '@/components/ChatWindow';
import NewChatModal from '@/components/NewChatModal';
import NewGroupModal from '@/components/NewGroupModal';

export default function ChatPage() {
  const router = useRouter();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [currentUserEmail, setCurrentUserEmail] = useState('');
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [isNewChatModalOpen, setIsNewChatModalOpen] = useState(false);
  const [isNewGroupModalOpen, setIsNewGroupModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mounted) return;

    const token = getToken();
    const email = getUserEmail();
    
    if (!token || !email) {
      console.log('No authentication found, redirecting to login');
      router.push('/login');
      return;
    }

    console.log('User authenticated:', email);
    initializeChat(token);

    return () => {
      websocketService.disconnect();
    };
  }, [mounted, router]);

  const initializeChat = async (token: string) => {
    try {
      console.log('Initializing chat...');
      
      // Get current user
      const user = await chatApi.getCurrentUser();
      console.log('User loaded:', user.email);
      setCurrentUserEmail(user.email);
      setCurrentUserId(user.id);

      // Load conversations
      const convs = await chatApi.getConversations();
      console.log('Conversations loaded:', convs.length);
      setConversations(convs);

      // Connect to WebSocket
      websocketService.connect(token, () => {
        console.log('WebSocket connected successfully');
      });

      setIsLoading(false);
    } catch (error) {
      console.error('Error initializing chat:', error);
      // If authentication fails, clear token and redirect
      if (error instanceof Error && error.message.includes('401')) {
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        router.push('/login');
      } else {
        setIsLoading(false);
        alert('Failed to load chat. Please try again.');
      }
    }
  };

  const handleSelectConversation = async (conversation: Conversation) => {
    setSelectedConversation(conversation);

    // Load messages
    try {
      const msgs = await chatApi.getMessages(conversation.id);
      setMessages(msgs.reverse()); // Reverse to show oldest first

      // Subscribe to new messages
      websocketService.subscribeToConversation(conversation.id, (message) => {
        setMessages((prev) => {
          // Prevent duplicate messages by checking if message already exists
          const exists = prev.some(m => m.id === message.id);
          if (exists) return prev;
          return [...prev, message];
        });
      });
    } catch (error) {
      console.error('Error loading messages:', error);
    }
  };

  const handleSendMessage = (content: string) => {
    if (!selectedConversation) return;

    websocketService.sendMessage(selectedConversation.id, content);
  };

  const handleCreateDirectChat = async (userId: number) => {
    try {
      const conversation = await chatApi.createDirectConversation(userId);
      setConversations((prev) => [conversation, ...prev]);
      handleSelectConversation(conversation);
    } catch (error) {
      console.error('Error creating direct chat:', error);
      alert('Failed to create chat');
    }
  };

  const handleCreateGroup = async (name: string, participantIds: number[]) => {
    try {
      const conversation = await chatApi.createGroupConversation({ name, participantIds });
      setConversations((prev) => [conversation, ...prev]);
      handleSelectConversation(conversation);
    } catch (error) {
      console.error('Error creating group:', error);
      alert('Failed to create group');
    }
  };

  if (!mounted || isLoading) {
    return (
      <div className="flex items-center justify-center h-screen bg-background">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-xl text-foreground">Loading chat...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-screen">
      <ConversationList
        conversations={conversations}
        selectedConversationId={selectedConversation?.id || null}
        currentUserEmail={currentUserEmail}
        onSelectConversation={handleSelectConversation}
        onNewChat={() => setIsNewChatModalOpen(true)}
        onNewGroup={() => setIsNewGroupModalOpen(true)}
      />
      <ChatWindow
        conversation={selectedConversation}
        messages={messages}
        currentUserEmail={currentUserEmail}
        currentUserId={currentUserId}
        onSendMessage={handleSendMessage}
      />
      <NewChatModal
        isOpen={isNewChatModalOpen}
        onClose={() => setIsNewChatModalOpen(false)}
        onCreateChat={handleCreateDirectChat}
      />
      <NewGroupModal
        isOpen={isNewGroupModalOpen}
        onClose={() => setIsNewGroupModalOpen(false)}
        onCreateGroup={handleCreateGroup}
      />
    </div>
  );
}
