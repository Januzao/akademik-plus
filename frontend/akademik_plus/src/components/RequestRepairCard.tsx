"use client";

import { useEffect, useState } from "react";
import { apiFetch, handleResponse } from "../api/client";
import { createMaintenanceRequest } from "../api/MaintenanceApi";
import { fetchRooms } from "../api/RoomsApi";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

interface UserProfile {
  id: number;
  role?: string;
  roomId?: number;
  roomNumber?: string;
}

interface RequestRepairCardProps {
  onCancel: () => void;
  onSubmitted?: () => void;
}

export default function RequestRepairCard({ onCancel, onSubmitted }: RequestRepairCardProps) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [rooms, setRooms] = useState<RoomResponseDTO[]>([]);
  const [selectedRoomId, setSelectedRoomId] = useState<number | "">("");
  const [category, setCategory] = useState("");
  const [priority, setPriority] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const isAdmin = user?.role === "ADMIN";

  useEffect(() => {
    apiFetch("/api/auth/me")
      .then(res => handleResponse<UserProfile>(res))
      .then(data => {
        setUser(data);
        if (data.role === "ADMIN") {
          fetchRooms().then(setRooms).catch(() => {});
        }
      })
      .catch(() => {});
  }, []);

  const effectiveRoomId = isAdmin ? (selectedRoomId === "" ? undefined : selectedRoomId) : user?.roomId;

  const handleSubmit = async () => {
    setError(null);

    if (!effectiveRoomId) {
      setError(
        isAdmin
          ? "Please select a room."
          : "You don't have a room assigned yet. Contact the administrator."
      );
      return;
    }
    if (!user) return;
    if (!category) { setError("Please select an issue category."); return; }
    if (!priority) { setError("Please select a priority level."); return; }
    if (!description.trim()) { setError("Please provide a description."); return; }

    setLoading(true);
    try {
      await createMaintenanceRequest(
        { roomId: effectiveRoomId as number, category: category as never, priority: priority as never, description },
        user.id
      );
      setSuccess(true);
      setTimeout(() => onSubmitted?.() ?? onCancel(), 1500);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to submit request.");
    } finally {
      setLoading(false);
    }
  };

  const selectClass = "w-full appearance-none bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 pr-9 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200";

  return (
    <div className="bg-white border border-green-100 shadow-sm p-8">

      <h1 className="text-2xl font-bold text-gray-900">Request for Fix</h1>
      <p className="text-sm text-gray-500 mt-1 mb-7">
        Submit a repair request for issues in your room or common areas
      </p>

      {/* Room field */}
      <div className="flex flex-col gap-1.5 mb-5">
        <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
            <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z"/>
          </svg>
          Room Number
        </label>

        {isAdmin ? (
          <div className="relative">
            <select
              value={selectedRoomId}
              onChange={e => setSelectedRoomId(e.target.value === "" ? "" : Number(e.target.value))}
              className={selectClass}
            >
              <option value="">Select a room</option>
              {rooms.map(r => (
                <option key={r.id} value={r.id}>
                  Room {r.roomNumber ?? r.id}
                  {r.floorNumber != null ? ` — Floor ${r.floorNumber}` : ""}
                </option>
              ))}
            </select>
            <svg className="size-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
            </svg>
          </div>
        ) : (
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700">
            {user
              ? user.roomNumber
                ? `Room ${user.roomNumber}`
                : <span className="text-orange-500">No room assigned — contact admin</span>
              : <span className="text-gray-400">Loading...</span>
            }
          </div>
        )}
      </div>

      {/* Issue Category */}
      <div className="flex flex-col gap-1.5 mb-5">
        <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M11.42 15.17 17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 0 0 4.486-6.336l-3.276 3.277a3.004 3.004 0 0 1-2.25-2.25l3.276-3.276a4.5 4.5 0 0 0-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437 1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008Z"/>
          </svg>
          Issue Category
        </label>
        <div className="relative">
          <select value={category} onChange={e => setCategory(e.target.value)} className={selectClass}>
            <option value="">Select issue type</option>
            <option value="PLUMBING">Plumbing</option>
            <option value="ELECTRICAL">Electrical</option>
            <option value="FURNITURE">Furniture</option>
            <option value="CLEANING">Cleaning</option>
            <option value="OTHER">Other</option>
          </select>
          <svg className="size-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
          </svg>
        </div>
      </div>

      {/* Priority Level */}
      <div className="flex flex-col gap-1.5 mb-5">
        <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z"/>
          </svg>
          Priority Level
        </label>
        <div className="relative">
          <select value={priority} onChange={e => setPriority(e.target.value)} className={selectClass}>
            <option value="">Select priority</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
          </select>
          <svg className="size-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
          </svg>
        </div>
      </div>

      {/* Description */}
      <div className="flex flex-col gap-1.5 mb-6">
        <label className="text-sm text-gray-800 font-medium">Description</label>
        <textarea
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="Please provide a detailed description of the issue..."
          rows={4}
          className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200 resize-none"
        />
        <p className="text-xs text-gray-400">Be as specific as possible to help us resolve the issue quickly</p>
      </div>

      {error && (
        <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}
      {success && (
        <div className="mb-4 rounded-lg bg-emerald-50 border border-emerald-200 px-4 py-3 text-sm text-emerald-700">
          Request submitted successfully!
        </div>
      )}

      <div className="h-px bg-green-100 w-full mb-5" />

      <div className="flex items-center justify-end gap-3">
        <button
          onClick={onCancel}
          disabled={loading}
          className="px-5 py-2.5 border border-gray-200 hover:bg-gray-50 text-gray-700 text-sm font-medium rounded-lg transition-colors disabled:opacity-50"
        >
          Cancel
        </button>
        <button
          onClick={handleSubmit}
          disabled={loading || success}
          className="flex items-center gap-1.5 px-5 py-2.5 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-60"
        >
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.8" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5"/>
          </svg>
          {loading ? "Submitting..." : "Submit Request"}
        </button>
      </div>
    </div>
  );
}
