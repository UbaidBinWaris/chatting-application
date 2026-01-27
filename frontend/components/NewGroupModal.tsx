'use client';

import { useState } from 'react';
import { chatApi, User } from '@/lib/chat-api';

interface NewGroupModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateGroup: (name: string, participantIds: number[]) => void;
}

export default function NewGroupModal({ isOpen, onClose, onCreateGroup }: NewGroupModalProps) {
  const [groupName, setGroupName] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<User[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<User[]>([]);
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

  const handleAddUser = (user: User) => {
    if (!selectedUsers.find((u) => u.id === user.id)) {
      setSelectedUsers([...selectedUsers, user]);
      setSearchQuery('');
      setSearchResults([]);
    }
  };

  const handleRemoveUser = (userId: number) => {
    setSelectedUsers(selectedUsers.filter((u) => u.id !== userId));
  };

  const handleCreate = () => {
    if (!groupName.trim() || selectedUsers.length === 0) {
      alert('Please enter a group name and add at least one participant');
      return;
    }

    onCreateGroup(groupName, selectedUsers.map((u) => u.id));
    setGroupName('');
    setSelectedUsers([]);
    setSearchQuery('');
    setSearchResults([]);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
      <div className="bg-zinc-900 rounded-lg p-6 w-[500px] max-h-[80vh] flex flex-col text-white">
        <h2 className="text-2xl font-bold mb-4">Create New Group</h2>

        <input
          type="text"
          value={groupName}
          onChange={(e) => setGroupName(e.target.value)}
          placeholder="Group name..."
          className="mb-4 px-4 py-2 bg-zinc-800 border border-zinc-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-white placeholder-zinc-500"
        />

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

        {searchResults.length > 0 && (
          <div className="mb-4 max-h-40 overflow-y-auto border border-zinc-700 rounded-lg">
            {searchResults.map((user) => (
              <div
                key={user.id}
                onClick={() => handleAddUser(user)}
                className="px-4 py-2 hover:bg-zinc-800 cursor-pointer border-b border-zinc-700 last:border-b-0 transition"
              >
                <p className="font-semibold text-zinc-200">{user.email}</p>
              </div>
            ))}
          </div>
        )}

        <div className="mb-4">
          <h3 className="font-semibold mb-2 text-zinc-300">Selected Participants ({selectedUsers.length})</h3>
          <div className="flex flex-wrap gap-2">
            {selectedUsers.map((user) => (
              <div
                key={user.id}
                className="bg-blue-900 text-blue-100 px-3 py-1 rounded-full flex items-center gap-2 border border-blue-800"
              >
                <span>{user.email}</span>
                <button
                  onClick={() => handleRemoveUser(user.id)}
                  className="text-blue-300 hover:text-white font-bold"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={handleCreate}
            className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
          >
            Create Group
          </button>
          <button
            onClick={() => {
              setGroupName('');
              setSelectedUsers([]);
              setSearchQuery('');
              setSearchResults([]);
              onClose();
            }}
            className="flex-1 px-4 py-2 bg-zinc-700 text-zinc-100 rounded-lg hover:bg-zinc-600 transition"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
