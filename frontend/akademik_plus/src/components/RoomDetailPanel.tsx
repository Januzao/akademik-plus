import { useEffect, useState } from "react";
import { apiFetch, handleResponse } from "../api/client";
import { fetchUsers } from "../api/UsersApi";
import { fetchMaintenanceRequests, createMaintenanceRequest } from "../api/MaintenanceApi";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";
import type { MaintenanceRequestRespDTO } from "../dto/MaintenanceRequestDTO";
import { useAuth } from "../hooks/AuthContext";

interface Resident {
  id: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
}

interface MeResponse { id: number; }

const PRIORITY_STYLES: Record<string, string> = {
  HIGH:   "bg-red-100 text-red-700",
  MEDIUM: "bg-yellow-100 text-yellow-700",
  LOW:    "bg-gray-100 text-gray-600",
  URGENT: "bg-red-200 text-red-800",
};

const STATUS_STYLES: Record<string, string> = {
  Pending:     "bg-yellow-100 text-yellow-700",
  "In Progress": "bg-blue-100 text-blue-700",
  Completed:   "bg-emerald-100 text-emerald-700",
  Cancelled:   "bg-gray-100 text-gray-500",
};

const TYPE_LABELS: Record<string, string> = {
  SINGLE: "Single",
  DOUBLE: "Double",
  TRIPLE: "Triple",
  QUAD:   "Quad",
};

interface RoomDetailPanelProps {
  room: RoomResponseDTO;
  onClose: () => void;
}

export default function RoomDetailPanel({ room, onClose }: RoomDetailPanelProps) {
  const { isAdmin } = useAuth();

  const [residents, setResidents]           = useState<Resident[]>([]);
  const [requests, setRequests]             = useState<MaintenanceRequestRespDTO[]>([]);
  const [loadingData, setLoadingData]       = useState(true);
  const [showForm, setShowForm]             = useState(false);

  // form state
  const [category, setCategory]   = useState("");
  const [priority, setPriority]   = useState("");
  const [description, setDesc]    = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError]   = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState(false);

  useEffect(() => {
    const roomNumber = room.roomNumber;
    const roomId     = room.id;

    const fetchData = async () => {
      try {
        if (isAdmin) {
          const [allUsers, allRequests] = await Promise.allSettled([
            fetchUsers() as Promise<Resident[]>,
            fetchMaintenanceRequests() as Promise<MaintenanceRequestRespDTO[]>,
          ]);

          if (allUsers.status === "fulfilled") {
            setResidents(
              allUsers.value.filter((u: any) =>
                (roomId != null && u.roomId === roomId) ||
                (roomNumber != null && u.roomNumber === roomNumber)
              )
            );
          }

          if (allRequests.status === "fulfilled") {
            setRequests(allRequests.value.filter(r => r.roomNumber === roomNumber));
          }
        } else {
          const { fetchMyMaintenanceRequests } = await import("../api/MaintenanceApi");
          const mine = await fetchMyMaintenanceRequests();
          setRequests(mine.filter(r => r.roomNumber === roomNumber));
        }
      } catch { /* silent */ }
      finally { setLoadingData(false); }
    };

    fetchData();
  }, [room.id, room.roomNumber, isAdmin]);

  const handleSubmit = async () => {
    setFormError(null);
    if (!category) { setFormError("Select a category."); return; }
    if (!priority) { setFormError("Select a priority."); return; }
    if (!description.trim()) { setFormError("Add a description."); return; }

    setSubmitting(true);
    try {
      const me = await apiFetch("/api/auth/me").then(r => handleResponse<MeResponse>(r));
      const created = await createMaintenanceRequest(
        { roomId: room.id, category: category as never, priority: priority as never, description },
        me.id
      );
      setRequests(prev => [created, ...prev]);
      setCategory(""); setPriority(""); setDesc("");
      setFormSuccess(true);
      setShowForm(false);
      setTimeout(() => setFormSuccess(false), 3000);
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "Failed to submit.");
    } finally {
      setSubmitting(false);
    }
  };

  const occupied    = room.occupiedPlaces ?? 0;
  const total       = room.totalPlaces ?? 0;
  const fillPercent = total > 0 ? Math.round((occupied / total) * 100) : 0;
  const rentPrice   = (room as any).rentPrice;

  const selectClass = "w-full appearance-none bg-gray-50 border border-gray-200 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200";

  return (
    <>
      <div className="fixed inset-0 bg-black/30 z-40" onClick={onClose} />

      <div className="fixed right-0 top-0 h-full w-full max-w-md bg-white shadow-xl z-50 flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4 shrink-0">
          <div>
            <h2 className="text-base font-semibold text-gray-900">
              Room {room.roomNumber}
            </h2>
            <p className="text-xs text-gray-500">
              Floor {room.floorNumber} · {TYPE_LABELS[String(room.roomType ?? "").toUpperCase()] ?? room.roomType}
              {rentPrice != null ? ` · ${Number(rentPrice).toFixed(0)} PLN/mo` : ""}
            </p>
          </div>
          <button onClick={onClose} className="rounded-lg p-1.5 text-gray-400 hover:bg-gray-100 transition-colors">
            <svg className="size-5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto px-6 py-5 space-y-6">

          {/* Occupancy bar */}
          <div className="space-y-1.5">
            <div className="flex justify-between text-xs text-gray-500">
              <span>Occupancy</span>
              <span className="font-medium text-gray-700">{occupied}/{total} places</span>
            </div>
            <div className="h-2 rounded-full bg-gray-100">
              <div
                className={`h-2 rounded-full transition-all ${fillPercent >= 100 ? "bg-red-400" : fillPercent >= 60 ? "bg-yellow-400" : "bg-emerald-400"}`}
                style={{ width: `${fillPercent}%` }}
              />
            </div>
          </div>

          <div className="h-px bg-gray-100" />

          {/* Residents (admin only) */}
          {isAdmin && (
            <div className="space-y-3">
              <h3 className="text-xs font-semibold uppercase tracking-wide text-gray-400">Residents</h3>
              {loadingData ? (
                <p className="text-sm text-gray-400">Loading...</p>
              ) : residents.length === 0 ? (
                <p className="text-sm text-gray-400 italic">No residents assigned.</p>
              ) : (
                <div className="space-y-2">
                  {residents.map(r => (
                    <div key={r.id} className="flex items-center gap-3 rounded-lg border border-gray-100 bg-gray-50 px-3 py-2.5">
                      <div className="size-8 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-700 text-xs font-bold shrink-0">
                        {(r.firstName?.[0] ?? "")}{(r.lastName?.[0] ?? "")}
                      </div>
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-gray-900 truncate">
                          {r.firstName} {r.lastName}
                        </p>
                        <p className="text-xs text-gray-400 truncate">{r.email}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {isAdmin && <div className="h-px bg-gray-100" />}

          {/* Maintenance requests */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-semibold uppercase tracking-wide text-gray-400">Maintenance Requests</h3>
              <button
                onClick={() => { setShowForm(f => !f); setFormError(null); }}
                className="flex items-center gap-1 text-xs font-medium text-green-700 hover:text-green-800 transition-colors"
              >
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d={showForm ? "M6 18 18 6M6 6l12 12" : "M12 4.5v15m7.5-7.5h-15"} />
                </svg>
                {showForm ? "Cancel" : "Add Request"}
              </button>
            </div>

            {formSuccess && (
              <div className="rounded-lg bg-emerald-50 border border-emerald-200 px-3 py-2 text-sm text-emerald-700">
                Request submitted successfully.
              </div>
            )}

            {/* Add form */}
            {showForm && (
              <div className="rounded-lg border border-green-200 bg-green-50 p-4 space-y-3">
                <div>
                  <label className="text-xs font-medium text-gray-600">Category</label>
                  <div className="relative mt-1">
                    <select value={category} onChange={e => setCategory(e.target.value)} className={selectClass}>
                      <option value="">Select category</option>
                      <option value="PLUMBING">Plumbing</option>
                      <option value="ELECTRICAL">Electrical</option>
                      <option value="FURNITURE">Furniture</option>
                      <option value="CLEANING">Cleaning</option>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>
                </div>
                <div>
                  <label className="text-xs font-medium text-gray-600">Priority</label>
                  <div className="relative mt-1">
                    <select value={priority} onChange={e => setPriority(e.target.value)} className={selectClass}>
                      <option value="">Select priority</option>
                      <option value="LOW">Low</option>
                      <option value="MEDIUM">Medium</option>
                      <option value="HIGH">High</option>
                      <option value="URGENT">Urgent</option>
                    </select>
                  </div>
                </div>
                <div>
                  <label className="text-xs font-medium text-gray-600">Description</label>
                  <textarea
                    value={description}
                    onChange={e => setDesc(e.target.value)}
                    rows={3}
                    placeholder="Describe the issue..."
                    className="mt-1 w-full rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200 resize-none"
                  />
                </div>
                {formError && <p className="text-xs text-red-600">{formError}</p>}
                <button
                  onClick={handleSubmit}
                  disabled={submitting}
                  className="w-full rounded-lg bg-green-700 py-2 text-sm font-medium text-white hover:bg-green-800 transition-colors disabled:opacity-60"
                >
                  {submitting ? "Submitting..." : "Submit Request"}
                </button>
              </div>
            )}

            {/* List */}
            {loadingData ? (
              <p className="text-sm text-gray-400">Loading...</p>
            ) : requests.length === 0 ? (
              <p className="text-sm text-gray-400 italic">No maintenance requests for this room.</p>
            ) : (
              <div className="space-y-2">
                {requests.map((req, i) => (
                  <div key={req.id ?? i} className="rounded-lg border border-gray-100 bg-white px-4 py-3 space-y-1.5">
                    <div className="flex items-start justify-between gap-2">
                      <p className="text-sm font-medium text-gray-900">
                        {req.category ? req.category[0] + req.category.slice(1).toLowerCase() : "Issue"}
                      </p>
                      <div className="flex gap-1.5 shrink-0">
                        {req.priority && (
                          <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${PRIORITY_STYLES[req.priority.toUpperCase()] ?? "bg-gray-100 text-gray-600"}`}>
                            {req.priority}
                          </span>
                        )}
                        {req.status && (
                          <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${STATUS_STYLES[req.status] ?? "bg-gray-100 text-gray-600"}`}>
                            {req.status}
                          </span>
                        )}
                      </div>
                    </div>
                    {req.description && (
                      <p className="text-xs text-gray-500 line-clamp-2">{req.description}</p>
                    )}
                    {req.tenantName && (
                      <p className="text-xs text-gray-400">By: {req.tenantName}</p>
                    )}
                    {req.requestDate && (
                      <p className="text-xs text-gray-400">
                        {new Date(req.requestDate).toLocaleDateString("uk-UA", { day: "2-digit", month: "short", year: "numeric" })}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
