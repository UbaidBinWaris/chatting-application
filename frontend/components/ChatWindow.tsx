'use client';

import { useEffect, useState, useRef } from 'react';
import { Conversation, Message } from '@/lib/websocket';
import { getToken } from '@/lib/auth';

interface ChatMessageProps {
  message: Message;
  isOwnMessage: boolean;
}

function FilePreview({ message }: { message: Message }) {
  const renderFileContent = () => {
    if (message.messageType === 'IMAGE' && message.fileUrl) {
      return (
        <div className="mt-2">
          <img 
            src={`http://localhost:8080${message.fileUrl}`}
            alt={message.fileName || 'Image'}
            className="max-w-xs max-h-64 rounded-lg cursor-pointer hover:opacity-90"
            onClick={() => window.open(`http://localhost:8080${message.fileUrl}`, '_blank')}
          />
        </div>
      );
    }

    if (message.messageType === 'VIDEO' && message.fileUrl) {
      return (
        <div className="mt-2">
          <video 
            src={`http://localhost:8080${message.fileUrl}`}
            controls
            className="max-w-xs max-h-64 rounded-lg"
          />
        </div>
      );
    }

    if (message.messageType === 'AUDIO' && message.fileUrl) {
      return (
        <div className="mt-2">
          <audio 
            src={`http://localhost:8080${message.fileUrl}`}
            controls
            className="w-full max-w-xs"
          />
        </div>
      );
    }

    if ((message.messageType === 'DOCUMENT' || message.messageType === 'FILE') && message.fileUrl) {
      const fileIcon = getFileIcon(message.fileName || '');
      const fileSize = formatFileSize(message.fileSize || 0);
      
      return (
        <div className="mt-2 flex items-center gap-3 bg-zinc-800 bg-opacity-50 p-3 rounded-lg max-w-xs">
          <div className="text-3xl">{fileIcon}</div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate">{message.fileName}</p>
            <p className="text-xs text-zinc-400">{fileSize}</p>
          </div>
          <a
            href={`http://localhost:8080${message.fileUrl.replace('/view/', '/download/')}`}
            download
            className="px-3 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700"
          >
            Download
          </a>
        </div>
      );
    }

    return null;
  };

  return renderFileContent();
}

function getFileIcon(fileName: string): string {
  const ext = fileName.split('.').pop()?.toLowerCase();
  
  const iconMap: { [key: string]: string } = {
    pdf: '📄',
    doc: '📝',
    docx: '📝',
    xls: '📊',
    xlsx: '📊',
    ppt: '📽️',
    pptx: '📽️',
    txt: '📃',
    zip: '🗜️',
    rar: '🗜️',
    '7z': '🗜️',
  };

  return iconMap[ext || ''] || '📎';
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

function ChatMessage({ message, isOwnMessage }: ChatMessageProps) {
  return (
    <div className={`mb-1 flex ${isOwnMessage ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-[70%] px-4 py-2 shadow-sm ${
          isOwnMessage
            ? 'bg-[#005c4b] text-white rounded-l-lg rounded-br-none rounded-tr-lg'
            : 'bg-[#202c33] text-zinc-100 rounded-r-lg rounded-bl-none rounded-tl-lg'
        }`}
      >
        {!isOwnMessage && (
          <div className="text-xs font-bold mb-1 text-orange-400">{message.senderEmail}</div>
        )}
        
        {message.messageType !== 'TEXT' && <FilePreview message={message} />}
        
        {message.content && (
          <div className="break-words text-sm leading-relaxed">{message.content}</div>
        )}
        
        <div className={`text-[10px] mt-1 text-right ${isOwnMessage ? 'text-zinc-300' : 'text-zinc-400'}`}>
          {new Date(message.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </div>
      </div>
    </div>
  );
}

interface ChatWindowProps {
  conversation: Conversation | null;
  messages: Message[];
  currentUserEmail: string;
  currentUserId: number | null;
  onSendMessage: (content: string) => void;
}

export default function ChatWindow({
  conversation,
  messages,
  currentUserEmail,
  currentUserId,
  onSendMessage,
}: ChatWindowProps) {
  const [messageInput, setMessageInput] = useState('');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFile || !conversation) return;

    setIsUploading(true);
    const formData = new FormData();
    formData.append('file', selectedFile);
    formData.append('conversationId', conversation.id.toString());
    if (messageInput.trim()) {
      formData.append('caption', messageInput.trim());
    }

    try {
      const token = getToken();
      const response = await fetch('http://localhost:8080/api/files/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: formData,
      });

      if (response.ok) {
        setSelectedFile(null);
        setMessageInput('');
        if (fileInputRef.current) {
          fileInputRef.current.value = '';
        }
      } else {
        alert('Failed to upload file');
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      alert('Error uploading file');
    } finally {
      setIsUploading(false);
    }
  };

  const handleSend = () => {
    if (selectedFile) {
      handleFileUpload();
    } else if (messageInput.trim() && conversation) {
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

  const removeSelectedFile = () => {
    setSelectedFile(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
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
      <div className="flex-1 overflow-y-auto px-6 py-4 bg-[#0b141a]"> {/* Added WhatsApp dark background color */}
        {messages.length === 0 ? (
          <div className="flex items-center justify-center h-full">
            <p className="text-zinc-500">No messages yet. Start the conversation!</p>
          </div>
        ) : (
          <div className="flex flex-col gap-2"> {/* Removed flex-col-reverse */}
            {messages.map((message) => (
              <ChatMessage
                key={message.id}
                message={message}
                isOwnMessage={message.senderId === currentUserId}
              />
            ))}
            <div ref={messagesEndRef} />
          </div>
        )}
      </div>

      {/* Input */}
      <div className="border-t border-zinc-800 px-6 py-4 bg-background">
        {selectedFile && (
          <div className="mb-3 flex items-center gap-3 bg-zinc-900 p-3 rounded-lg">
            <div className="text-2xl">{getFileIcon(selectedFile.name)}</div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-zinc-100 truncate">{selectedFile.name}</p>
              <p className="text-xs text-zinc-400">{formatFileSize(selectedFile.size)}</p>
            </div>
            <button
              onClick={removeSelectedFile}
              className="text-zinc-400 hover:text-red-500 transition"
            >
              ✕
            </button>
          </div>
        )}
        
        <div className="flex gap-2">
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileSelect}
            className="hidden"
            accept="image/*,video/*,audio/*,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar,.7z"
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            className="px-4 py-2 bg-zinc-800 text-zinc-100 rounded-lg hover:bg-zinc-700 transition"
            title="Attach file"
          >
            📎
          </button>
          <input
            type="text"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={selectedFile ? "Add a caption (optional)..." : "Type a message..."}
            className="flex-1 px-4 py-2 bg-zinc-900 border border-zinc-700 text-zinc-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 placeholder-zinc-500"
          />
          <button
            onClick={handleSend}
            disabled={(!messageInput.trim() && !selectedFile) || isUploading}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-zinc-800 disabled:text-zinc-500 disabled:cursor-not-allowed transition"
          >
            {isUploading ? 'Uploading...' : 'Send'}
          </button>
        </div>
      </div>
    </div>
  );
}
