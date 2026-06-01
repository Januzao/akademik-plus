import { useEffect, useState } from "react";
import MaintenanceFilters from "../components/MaintenanceFilters";
import MaintenanceStats from "../components/MaintenanceStats";
import MaintenanceRequestCard from "../components/MaintenanceRequestCard";
import { fetchMaintenanceRequests } from "../api/MaintenanceApi";
import type { MaintenanceFiltersState } from "../components/MaintenanceFilters";
import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";

export default function MaintenanceRequestsPage() {
  const [requests, setRequests] = useState<MaintenanceRequestReqDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<MaintenanceFiltersState>({
    status: "All Statuses",
    priority: "All Priorities",
  });

  /* ── fetch data ──────────────────────────────────────────── */
  useEffect(() => {
    let active = true;

    fetchMaintenanceRequests()
      .then((data) => {
        if (active) setRequests(data);
      })
      .catch((err) => {
        if (active) setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  /* ── filter logic ────────────────────────────────────────── */
  const filtered = requests.filter((r) => {
    const statusOk =
      filters.status === "All Statuses" || r.status === filters.status;
    const priorityOk =
      filters.priority === "All Priorities" || r.priority === filters.priority;
    return statusOk && priorityOk;
  });

  /* ── callbacks ───────────────────────────────────────────── */
  const handleStatusChange = (id: number, newStatus: string) => {
    setRequests((prev) =>
      prev.map((r) => (r.id === id ? { ...r, status: newStatus as MaintenanceRequestReqDTO["status"] } : r))
    );
    // TODO: call PATCH/PUT endpoint to persist the status change
  };

  const handleViewDetails = (request: MaintenanceRequestReqDTO) => {
    // TODO: navigate to detail page or open modal
    console.log("View details for", request.id);
  };

  /* ── render ──────────────────────────────────────────────── */
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
        {/* Back link */}
        <button
          type="button"
          onClick={() => window.history.back()}
          className="mb-4 flex items-center gap-1 text-sm text-gray-500 transition hover:text-gray-800"
        >
          ← Back to Admin Panel
        </button>

        {/* Header */}
        <h1 className="text-2xl font-bold text-gray-900">Maintenance Requests</h1>
        <p className="mt-1 text-sm text-gray-500">
          Review and manage tenant maintenance requests
        </p>

        {/* Filters */}
        <div className="mt-6">
          <MaintenanceFilters filters={filters} onChange={setFilters} />
        </div>

        {/* Stats */}
        <div className="mt-4">
          <MaintenanceStats requests={requests} />
        </div>

        {/* List */}
        <div className="mt-6 space-y-4">
          {loading && <p className="py-8 text-center text-sm text-gray-400">Loading requests…</p>}

          {error && (
            <p className="py-8 text-center text-sm text-red-500">Error: {error}</p>
          )}

          {!loading && !error && filtered.length === 0 && (
            <p className="py-8 text-center text-sm text-gray-400">
              No requests match the selected filters.
            </p>
          )}

          {filtered.map((req) => (
            <MaintenanceRequestCard
              key={req.id}
              request={req}
              onStatusChange={handleStatusChange}
              onViewDetails={handleViewDetails}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
