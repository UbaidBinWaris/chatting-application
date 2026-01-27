'use client';

import { useEffect, useState } from 'react';
import { Conversation, Message } from '@/lib/websocket';

interface ChatMessageProps {
  message: Message;
  isOwnMessage: boolean;
}

function ChatMessage({ message, isOwnMessage }: ChatMessageProps) {
  return (
    <div className={`mb-4 flex ${isOwnMessage ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-[70%] rounded-lg px-4 py-2 ${
          isOwnMessage
            ? 'bg-blue-600 text-white'
            : 'bg-zinc-800 text-zinc-100'
        }`}
      >
        {!isOwnMessage && (
          <div className="text-xs font-semibold mb-1 text-zinc-400">{message.senderEmail}</div>
        )}
        <div className="break-words">{message.content}</div>
        <div className={`text-xs mt-1 ${isOwnMessage ? 'text-blue-100' : 'text-zinc-400'}`}>
          {new Date(message.createdAt).toLocaleTimeString()}
        </div>
      </div>
    </div>
  );
}

interface ChatWindowProps {
  conversation: Conversation | null;
  messages: Message[];
  currentUserEmail: string;
  onSendMessage: (content: string) => void;
}

export default function ChatWindow({
  conversation,
  messages,
  currentUserEmail,
  onSendMessage,
}: ChatWindowProps) {
  const [messageInput, setMessageInput] = useState('');

  const handleSend = () => {
    if (messageInput.trim() && conversation) {
      onSendMessage(messageInput.trim());
      setMessageInput('');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  if (!conversation) {
    return (
      <div className="flex-1 flex items-center justify-center bg-background">
        <p className="text-zinc-400">Select a conversation to start chatting</p>
      </div>
    );
  }

  const conversationName = conversation.isGroup
    ? conversation.name
    : conversation.participants.find((p) => p.userEmail !== currentUserEmail)?.userEmail ||
      'Unknown';

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="bg-background border-b border-zinc-800 px-6 py-4">
        <h2 className="text-xl font-semibold text-zinc-100">{conversationName}</h2>
        {conversation.isGroup && (
          <p className="text-sm text-zinc-400">
            {conversation.participants.length} members
          </p>
        )}
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-6 py-4">
        {messages.length === 0 ? (
          <div className="flex items-center justify-center h-full">
            <p className="text-zinc-500">No messages yet. Start the conversation!</p>
          </div>
        ) : (
          <div className="flex flex-col-reverse">
            {messages.map((message) => (
              <ChatMessage
                key={message.id}
                message={message}
                isOwnMessage={message.senderEmail === currentUserEmail}
              />
            ))}
          </div>
        )}
      </div>

      {/* Input */}
      <div className="border-t border-zinc-800 px-6 py-4 bg-background">
        <div className="flex gap-2">
          <input
            type="text"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type a message..."
            className="flex-1 px-4 py-2 bg-zinc-900 border border-zinc-700 text-zinc-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 placeholder-zinc-500"
          />
          <button
            onClick={handleSend}
            disabled={!messageInput.trim()}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-zinc-800 disabled:text-zinc-500 disabled:cursor-not-allowed transition"
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
}
