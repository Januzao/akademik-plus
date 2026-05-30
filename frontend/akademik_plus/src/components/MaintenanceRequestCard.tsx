import { useState } from "react";
import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";

interface MaintenanceRequestCardProps {
  request: MaintenanceRequestReqDTO;
  onStatusChange?: (id: number, newStatus: string) => void;
  onViewDetails?: (request: MaintenanceRequestReqDTO) => void;
}

/* ── style maps ────────────────────────────────────────────── */
const PRIORITY_STYLES: Record<string, string> = {
  High: "border-red-200 bg-red-50 text-red-700",
  Medium: "border-yellow-200 bg-yellow-50 text-yellow-700",
  Low: "border-green-200 bg-green-50 text-green-700",
};

const STATUS_STYLES: Record<string, string> = {
  Pending: "border-gray-200 bg-gray-50 text-gray-600",
  "In Progress": "border-blue-200 bg-blue-50 text-blue-700",
  Completed: "border-green-200 bg-green-50 text-green-700",
};

const CATEGORY_ICONS: Record<string, string> = {
  Plumbing: "🔧",
  Electrical: "⚡",
  "Heating/Cooling": "❄️",
  "Door/Lock": "🔑",
  Cleaning: "🧹",
  Other: "🛠️",
};

/* ── helpers ───────────────────────────────────────────────── */
function formatDate(raw?: string): string {
  if (!raw) return "";
  const d = new Date(raw);
  return d.toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" });
}

/* ── component ─────────────────────────────────────────────── */
export default function MaintenanceRequestCard({
  request,
  onStatusChange,
  onViewDetails,
}: MaintenanceRequestCardProps) {
  const [localStatus, setLocalStatus] = useState(request.status ?? "Pending");

  const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
  const next = e.target.value;
  if (next === "Pending" || next === "In Progress" || next === "Completed") {
  setLocalStatus(next);
  onStatusChange?.(request.id!, next);
  }
  };

  const priority = request.priority ?? "Low";
  const category = request.category ?? "Other";
  const icon = CATEGORY_ICONS[category] ?? "🛠️";

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-5 py-4 transition hover:shadow-sm">
      {/* Row 1 — room number + badges */}
      <div className="flex flex-wrap items-center gap-2">
        <span className="text-sm font-semibold text-gray-800">
          Room {request.roomNumber ?? request.roomId}
        </span>

        <span
          className={`rounded-full border px-2 py-0.5 text-[11px] font-medium ${PRIORITY_STYLES[priority]}`}
        >
          {priority} Priority
        </span>

        <span
          className={`flex items-center gap-1 rounded-full border px-2 py-0.5 text-[11px] font-medium ${STATUS_STYLES[localStatus]}`}
        >
          <span className="text-[9px]">⊙</span> {localStatus}
        </span>
      </div>

      {/* Row 2 — category + date */}
      <div className="mt-2 flex items-center gap-1.5 text-xs text-gray-500">
        <span>{icon}</span>
        <span>{category}</span>
        <span className="text-gray-300">•</span>
        <span>{formatDate(request.requestDate)}</span>
      </div>

      {/* Row 3 — description */}
      <p className="mt-2 text-sm text-gray-700">{request.description}</p>

      {/* Row 4 — tenant info */}
      {(request.tenantName || request.tenantPhone) && (
        <div className="mt-2 flex flex-wrap gap-x-4 text-xs text-gray-500">
          {request.tenantName && (
            <span>
              Tenant: <strong className="font-medium text-gray-700">{request.tenantName}</strong>
            </span>
          )}
          {request.tenantPhone && <span>Phone: {request.tenantPhone}</span>}
        </div>
      )}

      {/* Row 5 — actions */}
      <div className="mt-4 flex items-center gap-4 border-t border-gray-100 pt-3">
        <select
          value={localStatus}
          onChange={handleStatusChange}
          className="rounded-md border border-gray-200 bg-white px-3 py-1.5 text-xs font-medium text-gray-700 outline-none transition focus:border-gray-400 focus:ring-1 focus:ring-gray-200"
        >
          <option value="Pending">Pending</option>
          <option value="In Progress">In Progress</option>
          <option value="Completed">Completed</option>
        </select>

        <button
          type="button"
          onClick={() => onViewDetails?.(request)}
          className="text-xs font-medium text-gray-600 transition hover:text-gray-900"
        >
          View Details
        </button>
      </div>
    </div>
  );
}
