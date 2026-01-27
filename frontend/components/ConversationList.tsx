'use client';

import { Conversation } from '@/lib/websocket';
import { useRouter } from 'next/navigation';
import { clearAuth } from '@/lib/auth';

interface ConversationListProps {
  conversations: Conversation[];
  selectedConversationId: number | null;
  currentUserEmail: string;
  onSelectConversation: (conversation: Conversation) => void;
  onNewChat: () => void;
  onNewGroup: () => void;
}

export default function ConversationList({
  conversations,
  selectedConversationId,
  currentUserEmail,
  onSelectConversation,
  onNewChat,
  onNewGroup,
}: ConversationListProps) {
  const router = useRouter();

  const handleLogout = () => {
    clearAuth();
    router.push('/login');
  };

  const handleBackToDashboard = () => {
    router.push('/dashboard');
  };
  const getConversationName = (conversation: Conversation) => {
    if (conversation.isGroup) {
      return conversation.name || 'Unnamed Group';
    }
    const otherParticipant = conversation.participants.find(
      (p) => p.userEmail !== currentUserEmail
    );
    return otherParticipant?.userEmail || 'Unknown';
  };

  const getLastMessagePreview = (conversation: Conversation) => {
    if (!conversation.lastMessage) {
      return 'No messages yet';
    }
    return conversation.lastMessage.content.length > 40
      ? conversation.lastMessage.content.substring(0, 40) + '...'
      : conversation.lastMessage.content;
  };

  return (
    <div className="w-80 bg-zinc-900 border-r border-zinc-800 flex flex-col">
      {/* Header */}
      <div className="bg-zinc-900 text-white px-6 py-4 border-b border-zinc-800">
        <div className="flex justify-between items-center mb-3">
          <h1 className="text-2xl font-bold">Chats</h1>
          <button
            onClick={handleLogout}
            className="px-3 py-1 bg-red-600 hover:bg-red-700 rounded text-sm font-semibold transition"
            title="Logout"
          >
            Logout
          </button>
        </div>
        <button
          onClick={handleBackToDashboard}
          className="w-full px-3 py-2 bg-zinc-800 hover:bg-zinc-700 rounded text-sm font-semibold flex items-center justify-center gap-2 transition"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Dashboard
        </button>
      </div>

      {/* New Chat Buttons */}
      <div className="p-4 border-b border-zinc-800">
        <button
          onClick={onNewChat}
          className="w-full mb-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          + New Chat
        </button>
        <button
          onClick={onNewGroup}
          className="w-full px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
        >
          + New Group
        </button>
      </div>

      {/* Conversations */}
      <div className="flex-1 overflow-y-auto">
        {conversations.length === 0 ? (
          <div className="p-6 text-center text-zinc-500">
            <p>No conversations yet</p>
            <p className="text-sm mt-2">Start a new chat to get started!</p>
          </div>
        ) : (
          conversations.map((conversation) => (
            <div
              key={conversation.id}
              onClick={() => onSelectConversation(conversation)}
              className={`px-6 py-4 border-b border-zinc-800 cursor-pointer hover:bg-zinc-800 transition ${
                selectedConversationId === conversation.id ? 'bg-zinc-800' : ''
              }`}
            >
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <h3 className="font-semibold text-zinc-200">
                    {getConversationName(conversation)}
                  </h3>
                  <p className="text-sm text-zinc-400 mt-1">
                    {getLastMessagePreview(conversation)}
                  </p>
                </div>
                {conversation.unreadCount > 0 && (
                  <span className="ml-2 bg-blue-600 text-white text-xs rounded-full px-2 py-1">
                    {conversation.unreadCount}
                  </span>
                )}
              </div>
              {conversation.lastMessage && (
                <p className="text-xs text-zinc-500 mt-1">
                  {new Date(conversation.lastMessage.createdAt).toLocaleTimeString()}
                </p>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
