import { useEffect, useState } from "react";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";
import { patchUser } from "../api/UsersApi";
import { fetchBillsByUser, createBill, cancelBill, type BillDTO } from "../api/BillsApi";

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

const BILL_STATUS_STYLES: Record<string, string> = {
  PENDING:   "bg-yellow-100 text-yellow-700",
  PAID:      "bg-emerald-100 text-emerald-700",
  CANCELLED: "bg-gray-100 text-gray-500",
};

function formatDate(raw?: string | null): string {
  if (!raw) return "";
  return new Date(raw).toLocaleDateString("uk-UA", { day: "2-digit", month: "short", year: "numeric" });
}

export default function AdminUserEditPanel({ user, rooms, onClose, onSaved }: AdminUserEditPanelProps) {
  // room / status
  const [roomId, setRoomId] = useState<number | "">(user.roomId ?? "");
  const [isActive, setIsActive] = useState(user.isActive ?? true);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  // bills list
  const [bills, setBills] = useState<BillDTO[]>([]);
  const [billsLoading, setBillsLoading] = useState(true);

  // new bill form
  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [issuing, setIssuing] = useState(false);
  const [billError, setBillError] = useState<string | null>(null);

  useEffect(() => {
    fetchBillsByUser(user.id)
      .then(setBills)
      .catch(() => {})
      .finally(() => setBillsLoading(false));
  }, [user.id]);

  const handleSave = async () => {
    setSaving(true);
    setSaveError(null);
    try {
      await patchUser(user.id, {
        roomId: roomId === "" ? null : roomId,
        isActive,
      });
      onSaved({ ...user, roomId: roomId === "" ? undefined : roomId, isActive });
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  };

  const handleIssueBill = async () => {
    if (!title.trim()) { setBillError("Title is required"); return; }
    const amt = parseFloat(amount);
    if (!amount || isNaN(amt) || amt <= 0) { setBillError("Amount must be greater than 0"); return; }
    if (!dueDate) { setBillError("Due date is required"); return; }

    setIssuing(true);
    setBillError(null);
    try {
      const created = await createBill({
        userId: user.id,
        title: title.trim(),
        description: description.trim() || undefined,
        amount: amt,
        dueDate,
      });
      setBills(prev => [created, ...prev]);
      setTitle("");
      setDescription("");
      setAmount("");
      setDueDate("");
      setShowForm(false);
    } catch (err) {
      setBillError(err instanceof Error ? err.message : "Failed to issue bill");
    } finally {
      setIssuing(false);
    }
  };

  const handleCancelBill = async (id: number) => {
    try {
      const updated = await cancelBill(id);
      setBills(prev => prev.map(b => b.id === id ? updated : b));
    } catch { /* silent */ }
  };

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/30 z-40" onClick={onClose} />

      {/* Panel */}
      <div className="fixed right-0 top-0 h-full w-full max-w-md bg-white shadow-xl z-50 flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4 shrink-0">
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

          {/* User info */}
          <div className="flex items-center gap-4">
            <div className="size-14 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-700 font-bold text-lg shrink-0">
              {getInitials(user.firstName, user.lastName)}
            </div>
            <div>
              <p className="text-sm font-semibold text-gray-900">{user.firstName} {user.lastName}</p>
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

          {/* Account Status */}
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

          {saveError && (
            <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
              {saveError}
            </div>
          )}

          <div className="h-px bg-gray-100" />

          {/* Bills section */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <p className="flex items-center gap-1.5 text-xs font-medium text-gray-500">
                <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 14.25l6-6m4.5-3.493V21.75l-3.75-1.5-3.75 1.5-3.75-1.5-3.75 1.5V4.757c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0 1 11.186 0c1.1.128 1.907 1.077 1.907 2.185Z" />
                </svg>
                Bills
              </p>
              <button
                type="button"
                onClick={() => { setShowForm(f => !f); setBillError(null); }}
                className="flex items-center gap-1 text-xs font-medium text-green-700 hover:text-green-800 transition-colors"
              >
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d={showForm ? "M6 18 18 6M6 6l12 12" : "M12 4.5v15m7.5-7.5h-15"} />
                </svg>
                {showForm ? "Cancel" : "Issue Bill"}
              </button>
            </div>

            {/* Issue bill form */}
            {showForm && (
              <div className="rounded-lg border border-green-200 bg-green-50 p-4 space-y-3">
                <div>
                  <label className="text-xs font-medium text-gray-600">Title *</label>
                  <input
                    type="text"
                    value={title}
                    onChange={e => setTitle(e.target.value)}
                    placeholder="e.g. Monthly rent, Utilities"
                    className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-green-600 focus:outline-none focus:ring-1 focus:ring-green-600"
                  />
                </div>
                <div>
                  <label className="text-xs font-medium text-gray-600">Description</label>
                  <textarea
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                    placeholder="Optional details..."
                    rows={2}
                    className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-green-600 focus:outline-none focus:ring-1 focus:ring-green-600 resize-none"
                  />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-medium text-gray-600">Amount (PLN) *</label>
                    <input
                      type="number"
                      min="0.01"
                      step="0.01"
                      value={amount}
                      onChange={e => setAmount(e.target.value)}
                      placeholder="0.00"
                      className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-green-600 focus:outline-none focus:ring-1 focus:ring-green-600"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-gray-600">Due Date *</label>
                    <input
                      type="date"
                      value={dueDate}
                      onChange={e => setDueDate(e.target.value)}
                      className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-green-600 focus:outline-none focus:ring-1 focus:ring-green-600"
                    />
                  </div>
                </div>
                {billError && (
                  <p className="text-xs text-red-600">{billError}</p>
                )}
                <button
                  type="button"
                  onClick={handleIssueBill}
                  disabled={issuing}
                  className="w-full rounded-lg bg-green-700 py-2 text-sm font-medium text-white hover:bg-green-800 transition-colors disabled:opacity-60"
                >
                  {issuing ? "Issuing..." : "Issue Bill"}
                </button>
              </div>
            )}

            {/* Bills list */}
            {billsLoading ? (
              <p className="text-xs text-gray-400">Loading bills...</p>
            ) : bills.length === 0 ? (
              <p className="text-xs text-gray-400">No bills issued yet.</p>
            ) : (
              <div className="space-y-2">
                {bills.map(bill => (
                  <div key={bill.id} className="rounded-lg border border-gray-200 bg-white px-4 py-3">
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-semibold text-gray-900 truncate">{bill.title}</p>
                        {bill.description && (
                          <p className="text-xs text-gray-500 mt-0.5 line-clamp-2">{bill.description}</p>
                        )}
                        <p className="text-xs text-gray-400 mt-1">
                          Due: {formatDate(bill.dueDate)}
                          {bill.paidDate && ` · Paid: ${formatDate(bill.paidDate)}`}
                        </p>
                      </div>
                      <div className="flex flex-col items-end gap-1.5 shrink-0">
                        <span className="text-sm font-bold text-gray-900">
                          {bill.amount.toFixed(2)} PLN
                        </span>
                        <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${BILL_STATUS_STYLES[bill.status] ?? "bg-gray-100 text-gray-500"}`}>
                          {bill.status}
                        </span>
                      </div>
                    </div>
                    {bill.status === "PENDING" && (
                      <button
                        type="button"
                        onClick={() => handleCancelBill(bill.id)}
                        className="mt-2 text-xs text-red-500 hover:text-red-700 transition-colors"
                      >
                        Cancel bill
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="border-t border-gray-100 px-6 py-4 flex gap-3 shrink-0">
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
