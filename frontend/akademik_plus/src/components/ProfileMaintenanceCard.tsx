import { useEffect, useState } from "react";
import { fetchMyMaintenanceRequests } from "../api/MaintenanceApi";
import type { MaintenanceRequestRespDTO } from "../dto/MaintenanceRequestDTO";
import { API_BASE } from "../api/client";

const STATUS_CONFIG: Record<string, { label: string; dot: string; badge: string }> = {
  PENDING:     { label: "Pending",     dot: "bg-yellow-400", badge: "bg-yellow-50 text-yellow-700 border-yellow-200" },
  IN_PROGRESS: { label: "In Progress", dot: "bg-blue-400",   badge: "bg-blue-50 text-blue-700 border-blue-200" },
  RESOLVED:    { label: "Resolved",    dot: "bg-green-500",  badge: "bg-green-50 text-green-700 border-green-200" },
  CANCELLED:   { label: "Cancelled",   dot: "bg-gray-400",   badge: "bg-gray-50 text-gray-500 border-gray-200" },
};

const PRIORITY_CONFIG: Record<string, { label: string; color: string }> = {
  URGENT: { label: "Urgent", color: "text-red-600" },
  HIGH:   { label: "High",   color: "text-orange-500" },
  MEDIUM: { label: "Medium", color: "text-yellow-600" },
  LOW:    { label: "Low",    color: "text-gray-400" },
};

const CATEGORY_LABELS: Record<string, string> = {
  ELECTRICAL: "Electrical",
  PLUMBING:   "Plumbing",
  FURNITURE:  "Furniture",
  CLEANING:   "Cleaning",
  OTHER:      "Other",
};

function CategoryIcon({ category }: { category?: string }) {
  switch (category) {
    case "ELECTRICAL":
      return (
        <svg className="size-4 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="m3.75 13.5 10.5-11.25L12 10.5h8.25L9.75 21.75 12 13.5H3.75Z"/>
        </svg>
      );
    case "PLUMBING":
      return (
        <svg className="size-4 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M15.182 15.182a4.5 4.5 0 0 1-6.364 0M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM9.75 9.75c0 .414-.168.75-.375.75S9 10.164 9 9.75 9.168 9 9.375 9s.375.336.375.75Zm-.375 0h.008v.015h-.008V9.75Zm5.625 0c0 .414-.168.75-.375.75s-.375-.336-.375-.75.168-.75.375-.75.375.336.375.75Zm-.375 0h.008v.015h-.008V9.75Z"/>
        </svg>
      );
    case "FURNITURE":
      return (
        <svg className="size-4 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M20.25 7.5l-.625 10.632a2.25 2.25 0 0 1-2.247 2.118H6.622a2.25 2.25 0 0 1-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125Z"/>
        </svg>
      );
    case "CLEANING":
      return (
        <svg className="size-4 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 1-6.23-.693L4.2 14.003M19.8 15.3l.396 2.083A2.25 2.25 0 0 1 17.99 20H6.01a2.25 2.25 0 0 1-2.207-2.617l.396-2.083M4.2 14.003l-.396-2.083"/>
        </svg>
      );
    default:
      return (
        <svg className="size-4 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M11.42 15.17 17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l5.654-4.654m5.36-6.262a7.5 7.5 0 1 1-10.607 10.607"/>
        </svg>
      );
  }
}

function formatDate(raw?: string): string {
  if (!raw) return "";
  return new Date(raw).toLocaleDateString("en-GB", { day: "numeric", month: "short", year: "numeric" });
}

export default function ProfileMaintenanceCard() {
  const [requests, setRequests] = useState<MaintenanceRequestRespDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState<number | null>(null);

  useEffect(() => {
    fetchMyMaintenanceRequests()
      .then(setRequests)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7">
      <h2 className="text-lg font-semibold text-gray-800">Maintenance Requests</h2>
      <p className="text-xs text-gray-400 mt-0.5 mb-5">History of your repair requests</p>

      <div className="h-px bg-green-100 w-full mb-4" />

      {loading ? (
        <p className="text-sm text-gray-400 text-center py-8">Loading…</p>
      ) : requests.length === 0 ? (
        <div className="flex flex-col items-center gap-2 py-10 text-center">
          <svg className="size-8 text-gray-300" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M11.42 15.17 17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l5.654-4.654m5.36-6.262a7.5 7.5 0 1 1-10.607 10.607"/>
          </svg>
          <p className="text-sm text-gray-400">No maintenance requests yet.</p>
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {requests.map((req, i) => {
            const statusCfg = STATUS_CONFIG[req.status ?? ""] ?? { label: req.status ?? "", dot: "bg-gray-300", badge: "bg-gray-50 text-gray-500 border-gray-200" };
            const priorityCfg = PRIORITY_CONFIG[req.priority ?? ""] ?? { label: req.priority ?? "", color: "text-gray-400" };
            const isOpen = expanded === i;

            return (
              <div key={req.id ?? i} className="border border-gray-100 rounded-xl overflow-hidden">
                {/* Row header */}
                <button
                  type="button"
                  onClick={() => setExpanded(isOpen ? null : i)}
                  className="w-full flex items-center gap-3 px-4 py-3 text-left hover:bg-gray-50 transition-colors"
                >
                  <div className="size-8 rounded-lg bg-green-50 border border-green-100 flex items-center justify-center text-green-700 shrink-0">
                    <CategoryIcon category={req.category} />
                  </div>

                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-sm font-semibold text-gray-800">
                        {CATEGORY_LABELS[req.category ?? ""] ?? req.category ?? "Request"}
                      </span>
                      {req.roomNumber && (
                        <span className="text-xs text-gray-400">· Room {req.roomNumber}</span>
                      )}
                    </div>
                    <p className="text-xs text-gray-500 truncate mt-0.5">{req.description || "—"}</p>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <span className={`inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-xs font-medium ${statusCfg.badge}`}>
                      <span className={`size-1.5 rounded-full ${statusCfg.dot}`} />
                      {statusCfg.label}
                    </span>
                    <svg
                      className={`size-4 text-gray-400 transition-transform ${isOpen ? "rotate-180" : ""}`}
                      fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
                    </svg>
                  </div>
                </button>

                {/* Expanded details */}
                {isOpen && (
                  <div className="border-t border-gray-100 px-4 py-4 bg-gray-50">
                    <div className="grid grid-cols-2 gap-x-6 gap-y-3 text-xs">
                      <div>
                        <span className="text-gray-400 font-medium">Date submitted</span>
                        <p className="text-gray-700 mt-0.5">{formatDate(req.requestDate)}</p>
                      </div>
                      <div>
                        <span className="text-gray-400 font-medium">Priority</span>
                        <p className={`mt-0.5 font-semibold ${priorityCfg.color}`}>{priorityCfg.label}</p>
                      </div>
                      {req.roomNumber && (
                        <div>
                          <span className="text-gray-400 font-medium">Room</span>
                          <p className="text-gray-700 mt-0.5">{req.roomNumber}</p>
                        </div>
                      )}
                      <div>
                        <span className="text-gray-400 font-medium">Status</span>
                        <p className="text-gray-700 mt-0.5">{statusCfg.label}</p>
                      </div>
                      {req.description && (
                        <div className="col-span-2">
                          <span className="text-gray-400 font-medium">Description</span>
                          <p className="text-gray-700 mt-0.5 leading-relaxed">{req.description}</p>
                        </div>
                      )}
                      {req.photoUrl && (
                        <div className="col-span-2">
                          <span className="text-gray-400 font-medium block mb-1.5">Photo</span>
                          <img
                            src={`${API_BASE}${req.photoUrl}`}
                            alt="Request photo"
                            className="rounded-lg max-h-48 object-cover border border-gray-200"
                          />
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
