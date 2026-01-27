'use client';

import { useState } from 'react';
import { chatApi, User } from '@/lib/chat-api';

interface NewChatModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateChat: (userId: number) => void;
}

export default function NewChatModal({ isOpen, onClose, onCreateChat }: NewChatModalProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<User[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    setIsSearching(true);
    try {
      const users = await chatApi.searchUsers(searchQuery);
      setSearchResults(users);
    } catch (error) {
      console.error('Error searching users:', error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleSelectUser = (userId: number) => {
    onCreateChat(userId);
    setSearchQuery('');
    setSearchResults([]);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
      <div className="bg-zinc-900 rounded-lg p-6 w-96 max-h-[80vh] flex flex-col text-white">
        <h2 className="text-2xl font-bold mb-4">New Chat</h2>

        <div className="flex gap-2 mb-4">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            placeholder="Search users by email..."
            className="flex-1 px-4 py-2 bg-zinc-800 border border-zinc-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-white placeholder-zinc-500"
          />
          <button
            onClick={handleSearch}
            disabled={isSearching}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-zinc-700 transition"
          >
            {isSearching ? 'Searching...' : 'Search'}
          </button>
        </div>

        <div className="flex-1 overflow-y-auto mb-4">
          {searchResults.length === 0 ? (
            <p className="text-zinc-500 text-center py-4">
              {searchQuery ? 'No users found' : 'Search for users to start a chat'}
            </p>
          ) : (
            <div>
              {searchResults.map((user) => (
                <div
                  key={user.id}
                  onClick={() => handleSelectUser(user.id)}
                  className="px-4 py-3 border-b border-zinc-800 hover:bg-zinc-800 cursor-pointer transition"
                >
                  <p className="font-semibold text-zinc-200">{user.email}</p>
                  <p className="text-sm text-zinc-400">{user.role}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        <button
          onClick={onClose}
          className="w-full px-4 py-2 bg-zinc-700 text-white rounded-lg hover:bg-zinc-600 transition"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
