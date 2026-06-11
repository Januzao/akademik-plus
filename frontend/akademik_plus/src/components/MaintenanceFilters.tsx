export interface MaintenanceFiltersState {
  status: string;
  priority: string;
}

interface MaintenanceFiltersProps {
  filters: MaintenanceFiltersState;
  onChange: (filters: MaintenanceFiltersState) => void;
}

const STATUS_OPTIONS: { value: string; label: string }[] = [
  { value: "All Statuses", label: "All Statuses" },
  { value: "PENDING",      label: "Pending" },
  { value: "IN_PROGRESS",  label: "In Progress" },
  { value: "RESOLVED",     label: "Resolved" },
  { value: "CANCELLED",    label: "Cancelled" },
];

const PRIORITY_OPTIONS: { value: string; label: string }[] = [
  { value: "All Priorities", label: "All Priorities" },
  { value: "URGENT",         label: "Urgent" },
  { value: "HIGH",           label: "High" },
  { value: "MEDIUM",         label: "Medium" },
  { value: "LOW",            label: "Low" },
];

export default function MaintenanceFilters({ filters, onChange }: MaintenanceFiltersProps) {
  return (
    <div className="border border-gray-200 bg-white p-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {/* Status */}
        <div>
          <label className="mb-1 block text-xs font-medium text-gray-500">Status</label>
          <select
            value={filters.status}
            onChange={(e) => onChange({ ...filters, status: e.target.value })}
            className="w-full appearance-none rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-800 outline-none transition focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
          >
            {STATUS_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>

        {/* Priority */}
        <div>
          <label className="mb-1 block text-xs font-medium text-gray-500">Priority</label>
          <select
            value={filters.priority}
            onChange={(e) => onChange({ ...filters, priority: e.target.value })}
            className="w-full appearance-none rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-800 outline-none transition focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
          >
            {PRIORITY_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
}
