import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";

interface MaintenanceStatsProps {
  requests: MaintenanceRequestReqDTO[];
}

export default function MaintenanceStats({ requests }: MaintenanceStatsProps) {
  const pending    = requests.filter((r) => r.status === "PENDING").length;
  const inProgress = requests.filter((r) => r.status === "IN_PROGRESS").length;
  const resolved   = requests.filter((r) => r.status === "RESOLVED").length;

  const items = [
    { label: "Pending Requests", count: pending },
    { label: "In Progress",      count: inProgress },
    { label: "Resolved",         count: resolved },
  ];

  return (
    <div className="grid grid-cols-3 divide-x divide-gray-200 border border-gray-200 bg-white">
      {items.map((item) => (
        <div key={item.label} className="flex flex-col items-center py-5">
          <span className="text-2xl font-bold text-gray-800">{item.count}</span>
          <span className="mt-1 text-xs text-gray-500">{item.label}</span>
        </div>
      ))}
    </div>
  );
}
