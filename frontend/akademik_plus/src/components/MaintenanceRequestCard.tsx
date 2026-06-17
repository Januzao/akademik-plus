import { useState } from "react";
import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";
import { updateMaintenanceStatus } from "../api/MaintenanceApi";
import { API_BASE } from "../api/client";
import MaintenanceChatThread from "./MaintenanceChatThread";

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
  PENDING:     "border-gray-200 bg-gray-50 text-gray-600",
  IN_PROGRESS: "border-blue-200 bg-blue-50 text-blue-700",
  RESOLVED:    "border-green-200 bg-green-50 text-green-700",
  CANCELLED:   "border-red-200 bg-red-50 text-red-600",
};

const STATUS_LABELS: Record<string, string> = {
  PENDING:     "Pending",
  IN_PROGRESS: "In Progress",
  RESOLVED:    "Resolved",
  CANCELLED:   "Cancelled",
};

const CATEGORY_ICONS: Record<string, string> = {
  Plumbing: "🔧",
  Electrical: "⚡",
  "Heating/Cooling": "❄️",
  "Door/Lock": "🔑",
  Cleaning: "🧹",
  Other: "🛠️",
};

function formatDate(raw?: string): string {
  if (!raw) return "";
  const d = new Date(raw);
  return d.toLocaleDateString("en-US", {
    month: "long",
    day: "numeric",
    year: "numeric",
  });
}

export default function MaintenanceRequestCard({
  request,
  onStatusChange,
  onViewDetails,
}: MaintenanceRequestCardProps) {
  const [localStatus, setLocalStatus] = useState<string>(request.status ?? "PENDING");
  const [isUpdating, setIsUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showDetails, setShowDetails] = useState(false);

  const handleStatusChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const next = e.target.value;
    const prevStatus = localStatus;
    setLocalStatus(next);
    setError(null);
    setIsUpdating(true);

    try {
      await updateMaintenanceStatus(request.id!, next);
      onStatusChange?.(request.id!, next);
    } catch (err) {
      setLocalStatus(prevStatus);
      setError("Failed to update status");
      console.error("Status update error:", err);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleViewDetails = () => {
    setShowDetails((prev) => !prev);
    onViewDetails?.(request);
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
          className={`flex items-center gap-1 rounded-full border px-2 py-0.5 text-[11px] font-medium ${STATUS_STYLES[localStatus] ?? STATUS_STYLES.PENDING}`}
        >
          <span className="text-[9px]">⊙</span> {STATUS_LABELS[localStatus] ?? localStatus}
        </span>
      </div>
      <div className="mt-2 flex items-center gap-1.5 text-xs text-gray-500">
        <span>{icon}</span>
        <span>{category}</span>
        <span className="text-gray-300">•</span>
        <span>{formatDate(request.requestDate)}</span>
      </div>
      <p className="mt-2 text-sm text-gray-700">{request.description}</p>

      {request.photoUrl && (
        <img
          src={`${API_BASE}${request.photoUrl}`}
          alt="Maintenance issue"
          className="mt-2 max-h-36 rounded-md border border-gray-200 object-cover"
          onError={e => { (e.target as HTMLImageElement).style.display = "none"; }}
        />
      )}

      {(request.tenantName || request.tenantPhone) && (
        <div className="mt-2 flex flex-wrap gap-x-4 text-xs text-gray-500">
          {request.tenantName && (
            <span>
              Tenant:{" "}
              <strong className="font-medium text-gray-700">
                {request.tenantName}
              </strong>
            </span>
          )}
          {request.tenantPhone && <span>Phone: {request.tenantPhone}</span>}
        </div>
      )}

      {error && (
        <p className="mt-2 text-xs text-red-600">{error}</p>
      )}

      <div className="mt-4 flex items-center gap-4 border-t border-gray-100 pt-3">
        <div className="relative">
          <select
            value={localStatus}
            onChange={handleStatusChange}
            disabled={isUpdating}
            className={`rounded-md border border-gray-200 bg-white px-3 py-1.5 text-xs font-medium text-gray-700 outline-none transition focus:border-gray-400 focus:ring-1 focus:ring-gray-200 ${
              isUpdating ? "cursor-not-allowed opacity-50" : ""
            }`}
          >
            <option value="PENDING">Pending</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          {isUpdating && (
            <span className="ml-2 text-xs text-gray-400">Saving…</span>
          )}
        </div>

        <button
          type="button"
          onClick={handleViewDetails}
          className="text-xs font-medium text-gray-600 transition hover:text-gray-900"
        >
          {showDetails ? "Hide Details" : "View Details"}
        </button>
      </div>

      {showDetails && (
        <div className="mt-3 rounded-md border border-gray-100 bg-gray-50 p-4">
          <h4 className="mb-3 text-xs font-semibold uppercase tracking-wide text-gray-400">
            Full Details
          </h4>

          <dl className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
            <DetailRow label="ID" value={request.id} />
            <DetailRow label="Room" value={request.roomNumber ?? request.roomId} />
            <DetailRow label="Category" value={`${icon} ${category}`} />
            <DetailRow label="Priority" value={priority} />
            <DetailRow label="Status" value={STATUS_LABELS[localStatus] ?? localStatus} />
            <DetailRow label="Date" value={formatDate(request.requestDate)} />
            <DetailRow label="Tenant" value={request.tenantName} />
            <DetailRow label="Phone" value={request.tenantPhone} />
          </dl>

          <div className="mt-3">
            <span className="text-xs font-medium text-gray-400">Description</span>
            <p className="mt-0.5 text-sm text-gray-700">{request.description}</p>
          </div>

          {request.photoUrl && (
            <div className="mt-3">
              <span className="text-xs font-medium text-gray-400">Photo</span>
              <img
                src={`${API_BASE}${request.photoUrl}`}
                alt="Maintenance issue"
                className="mt-1 max-h-48 w-full rounded-md border border-gray-200 object-cover"
                onError={e => { (e.target as HTMLImageElement).style.display = "none"; }}
              />
            </div>
          )}

          {/* Chat thread */}
          {request.id != null && (
            <MaintenanceChatThread requestId={request.id} />
          )}
        </div>
      )}
    </div>
  );
}

/* ── small helper for the detail grid ── */
function DetailRow({
  label,
  value,
}: {
  label: string;
  value?: string | number | null;
}) {
  return (
    <div>
      <dt className="text-xs font-medium text-gray-400">{label}</dt>
      <dd className="text-sm text-gray-700">{value ?? "—"}</dd>
    </div>
  );
}