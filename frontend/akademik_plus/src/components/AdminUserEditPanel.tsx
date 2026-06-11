import { useState } from "react";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";
import { patchUser } from "../api/UsersApi";

export interface AdminUserDTO {
  id: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  role?: string;
  isActive?: boolean;
  roomId?: number;
}

interface AdminUserEditPanelProps {
  user: AdminUserDTO;
  rooms: RoomResponseDTO[];
  onClose: () => void;
  onSaved: (updated: AdminUserDTO) => void;
}

function getInitials(firstName?: string, lastName?: string) {
  const f = (firstName ?? "").trim().charAt(0);
  const l = (lastName ?? "").trim().charAt(0);
  return `${f}${l}`.toUpperCase() || "?";
}

export default function AdminUserEditPanel({ user, rooms, onClose, onSaved }: AdminUserEditPanelProps) {
  const [roomId, setRoomId] = useState<number | "">(user.roomId ?? "");
  const [isActive, setIsActive] = useState(user.isActive ?? true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      await patchUser(user.id, {
        roomId: roomId === "" ? null : roomId,
        isActive,
      });
      onSaved({ ...user, roomId: roomId === "" ? undefined : roomId, isActive });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/30 z-40"
        onClick={onClose}
      />

      {/* Panel */}
      <div className="fixed right-0 top-0 h-full w-full max-w-sm bg-white shadow-xl z-50 flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4">
          <h2 className="text-base font-semibold text-gray-900">Edit User</h2>
          <button
            onClick={onClose}
            className="rounded-lg p-1.5 text-gray-400 hover:bg-gray-100 hover:text-gray-600 transition-colors"
          >
            <svg className="size-5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto px-6 py-5 space-y-6">

          {/* User info (read-only) */}
          <div className="flex items-center gap-4">
            <div className="size-14 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-700 font-bold text-lg shrink-0">
              {getInitials(user.firstName, user.lastName)}
            </div>
            <div>
              <p className="text-sm font-semibold text-gray-900">
                {user.firstName} {user.lastName}
              </p>
              <p className="text-xs text-gray-500">{user.email}</p>
              {user.phone && <p className="text-xs text-gray-400">{user.phone}</p>}
            </div>
          </div>

          <div className="h-px bg-gray-100" />

          {/* Room assignment */}
          <div className="space-y-2">
            <label className="flex items-center gap-1.5 text-xs font-medium text-gray-500">
              <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 21v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21m0 0h4.5V3.545M12.75 21h7.5V10.75M2.25 21h1.5m18 0h-18M2.25 9l4.5-1.636M18.75 3l-1.5.545m0 6.205 3 1m1.5.5-1.5-.5M6.75 7.364V3h-3v18m3-13.636 10.5-3.819" />
              </svg>
              Room Assignment
            </label>
            <select
              value={roomId}
              onChange={e => setRoomId(e.target.value === "" ? "" : Number(e.target.value))}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-green-600 focus:outline-none focus:ring-1 focus:ring-green-600"
            >
              <option value="">— No room assigned —</option>
              {rooms.map(r => (
                <option key={r.id} value={r.id}>
                  {r.roomNumber ?? `Room ${r.id}`}
                  {r.floorNumber != null ? ` (Floor ${r.floorNumber})` : ""}
                  {r.occupiedPlaces != null && r.totalPlaces != null
                    ? ` — ${r.occupiedPlaces}/${r.totalPlaces}`
                    : ""}
                </option>
              ))}
            </select>
          </div>

          {/* Active / Disabled toggle */}
          <div className="space-y-2">
            <p className="flex items-center gap-1.5 text-xs font-medium text-gray-500">
              <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
              </svg>
              Account Status
            </p>
            <div className="flex gap-3">
              <button
                type="button"
                onClick={() => setIsActive(true)}
                className={`flex-1 rounded-lg border py-2.5 text-sm font-medium transition-colors ${
                  isActive
                    ? "border-emerald-500 bg-emerald-50 text-emerald-700"
                    : "border-gray-200 bg-white text-gray-500 hover:bg-gray-50"
                }`}
              >
                Active
              </button>
              <button
                type="button"
                onClick={() => setIsActive(false)}
                className={`flex-1 rounded-lg border py-2.5 text-sm font-medium transition-colors ${
                  !isActive
                    ? "border-red-400 bg-red-50 text-red-700"
                    : "border-gray-200 bg-white text-gray-500 hover:bg-gray-50"
                }`}
              >
                Disabled
              </button>
            </div>
          </div>

          {error && (
            <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="border-t border-gray-100 px-6 py-4 flex gap-3">
          <button
            onClick={onClose}
            disabled={saving}
            className="flex-1 rounded-lg border border-gray-200 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex-1 rounded-lg bg-green-700 py-2.5 text-sm font-medium text-white hover:bg-green-800 transition-colors disabled:opacity-60"
          >
            {saving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </>
  );
}
