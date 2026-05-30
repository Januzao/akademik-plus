import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";

interface MaintenanceStatsProps {
  requests: MaintenanceRequestReqDTO[];
}

export default function MaintenanceStats({ requests }: MaintenanceStatsProps) {
  const pending = requests.filter((r) => r.status === "Pending").length;
  const inProgress = requests.filter((r) => r.status === "In Progress").length;
  const completed = requests.filter((r) => r.status === "Completed").length;

  const items = [
    { label: "Pending Requests", count: pending },
    { label: "In Progress", count: inProgress },
    { label: "Completed", count: completed },
  ];

  return (
    <div className="grid grid-cols-3 divide-x divide-gray-200 rounded-lg border border-gray-200 bg-white">
      {items.map((item) => (
        <div key={item.label} className="flex flex-col items-center py-5">
          <span className="text-2xl font-bold text-gray-800">{item.count}</span>
          <span className="mt-1 text-xs text-gray-500">{item.label}</span>
        </div>
      ))}
    </div>
  );
}
