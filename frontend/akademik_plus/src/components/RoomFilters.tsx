import {
  ROOM_TYPES,
  OCCUPANCY_STATUSES,
} from "./roomFiltersUtils";
import type { RoomFilters } from "./roomFiltersUtils";

type ViewMode = "rooms" | "people";

interface RoomFiltersProps {
  filters: RoomFilters;
  onChange: (filters: RoomFilters) => void;
  mode: ViewMode;
  search: string;
  onSearchChange: (value: string) => void;
  onModeChange: (mode: ViewMode) => void;
}

export default function RoomFiltersBar({
  filters,
  onChange,
  mode,
  search,
  onSearchChange,
  onModeChange,
}: RoomFiltersProps) {
  const selectClass =
    "w-full appearance-none rounded-lg border border-gray-200 bg-white px-3 py-2 pr-8 text-sm text-gray-900 outline-none transition-colors hover:border-gray-300 focus:border-emerald-400 focus:ring-1 focus:ring-emerald-400";

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-5">
      {/* Top row: title + action buttons */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h3 className="text-lg font-bold text-gray-900">Filters</h3>

        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            className="inline-flex items-center gap-1.5 rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
            Add new person
          </button>
          <button
            type="button"
            className="inline-flex items-center gap-1.5 rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
            Maintenance Requests
          </button>
          <button
            type="button"
            onClick={() => onModeChange(mode === "rooms" ? "people" : "rooms")}
            className="inline-flex items-center gap-1.5 rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-4 w-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
            </svg>
            {mode === "rooms" ? "Person Mode" : "Room Mode"}
          </button>
        </div>
      </div>

      {mode == "people" && (
        <div className="mt-4">
          <label className="mb-1 block text-sm font-medium text-gray-600">
            Search by room number or person name
          </label>
          <input
            type="text"
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder="Type room number or person name..."
            className="w-full rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm text-gray-900 outline-none transition-colors focus:border-emerald-400 focus:ring-1 focus:ring-emerald-400"
          />
        </div>
      )}

      {/* Dropdowns row */}
      <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-600">
            Room Type
          </label>
          <div className="relative">
            <select
              value={filters.roomType}
              onChange={(e) =>
                onChange({ ...filters, roomType: e.target.value })
              }
              className={selectClass}
            >
              {ROOM_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
            <svg className="pointer-events-none absolute right-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z" clipRule="evenodd"/></svg>
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-600">
            Occupancy Status
          </label>
          <div className="relative">
            <select
              value={filters.occupancyStatus}
              onChange={(e) =>
                onChange({ ...filters, occupancyStatus: e.target.value })
              }
              className={selectClass}
            >
              {OCCUPANCY_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
            <svg className="pointer-events-none absolute right-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z" clipRule="evenodd"/></svg>
          </div>
        </div>
      </div>
    </div>
  );
}