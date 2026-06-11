import { useEffect, useState } from "react";
import { fetchMyRoomHistory, type RoomHistoryDTO } from "../api/RoomHistoryApi";

const ROOM_TYPE_LABELS: Record<string, string> = {
  DOUBLE: "Double",
  TRIPLE: "Triple",
  QUAD:   "Quad",
};

function formatDate(raw?: string | null): string {
  if (!raw) return "Present";
  return new Date(raw).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

export default function ProfileHousingHistoryCard() {
  const [history, setHistory] = useState<RoomHistoryDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMyRoomHistory()
      .then(setHistory)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7 flex flex-col">
      <h2 className="text-lg font-semibold text-gray-800 mb-5">Housing History</h2>

      <div className="h-px bg-green-100 w-full mb-4" />

      {loading ? (
        <p className="text-sm text-gray-400 text-center py-8">Loading history…</p>
      ) : history.length === 0 ? (
        <p className="text-sm text-gray-400 text-center py-8">No housing history found.</p>
      ) : (
        <div className="flex flex-col gap-3">
          {history.map((entry, i) => (
            <div
              key={entry.id}
              className="flex items-start gap-4 rounded-lg border border-gray-100 bg-gray-50 px-4 py-3"
            >
              {/* Timeline dot */}
              <div className="flex flex-col items-center pt-1">
                <span className={`size-2.5 rounded-full shrink-0 ${entry.checkOut ? "bg-gray-300" : "bg-green-500"}`} />
                {i < history.length - 1 && <div className="w-px flex-1 bg-gray-200 mt-1" style={{ minHeight: 20 }} />}
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2 flex-wrap">
                  <span className="text-sm font-semibold text-gray-800">
                    Room {entry.roomNumber}
                    {entry.floorNumber != null && (
                      <span className="ml-1 text-xs font-normal text-gray-500">— Floor {entry.floorNumber}</span>
                    )}
                  </span>
                  {!entry.checkOut && (
                    <span className="text-[11px] font-medium text-green-700 bg-green-50 border border-green-200 rounded-full px-2 py-0.5">
                      Current
                    </span>
                  )}
                </div>

                <div className="mt-1 flex flex-wrap gap-x-4 gap-y-0.5 text-xs text-gray-500">
                  <span>{formatDate(entry.checkIn)} — {formatDate(entry.checkOut)}</span>
                  {entry.roomType && <span>{ROOM_TYPE_LABELS[entry.roomType] ?? entry.roomType}</span>}
                  {entry.rentPrice != null && (
                    <span className="font-medium text-gray-600">{entry.rentPrice} PLN/mo</span>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
