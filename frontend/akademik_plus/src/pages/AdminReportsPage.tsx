import { useEffect, useState } from "react";
import { fetchAdminStats, type AdminStatsDTO } from "../api/AdminApi";

const ROOM_TYPE_LABELS: Record<string, string> = {
  DOUBLE: "Double (2-person)",
  TRIPLE: "Triple (3-person)",
  QUAD:   "Quad (4-person)",
};

function StatCard({ label, value, sub, accent }: { label: string; value: string | number; sub?: string; accent?: string }) {
  return (
    <div className="bg-white border border-gray-200 rounded-lg px-5 py-4 flex flex-col gap-1">
      <span className="text-xs text-gray-500">{label}</span>
      <span className={`text-2xl font-bold ${accent ?? "text-gray-800"}`}>{value}</span>
      {sub && <span className="text-xs text-gray-400">{sub}</span>}
    </div>
  );
}

export default function AdminReportsPage() {
  const [stats, setStats] = useState<AdminStatsDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    setError(null);
    fetchAdminStats()
      .then(setStats)
      .catch(() => setError("Failed to load statistics."))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const occupancyPct = stats && stats.totalPlaces > 0
    ? Math.round((stats.occupiedPlaces / stats.totalPlaces) * 100)
    : 0;

  return (
    <div className="bg-[#f0f4f0] min-h-screen py-8">
      <div className="max-w-7xl mx-auto px-4 flex flex-col gap-6">

        {/* Page header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Reports & Statistics</h1>
            <p className="text-sm text-gray-500 mt-0.5">Occupancy overview, financial status and arrears</p>
          </div>
          <button
            onClick={load}
            disabled={loading}
            className="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm text-gray-700 hover:bg-gray-50 disabled:opacity-50 transition-colors"
          >
            <svg className={`size-4 ${loading ? "animate-spin" : ""}`} fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"/>
            </svg>
            Refresh
          </button>
        </div>

        {error && (
          <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{error}</div>
        )}

        {loading && !stats && (
          <p className="text-sm text-gray-400 text-center py-16">Loading statistics…</p>
        )}

        {stats && (
          <>
            {/* Occupancy section */}
            <section>
              <h2 className="text-sm font-semibold uppercase tracking-wide text-gray-400 mb-3">Occupancy</h2>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
                <StatCard label="Total rooms" value={stats.totalRooms} />
                <StatCard label="Vacant rooms" value={stats.vacantRooms} accent="text-green-700" />
                <StatCard label="Full rooms" value={stats.fullRooms} accent="text-orange-600" />
                <StatCard label="Total places" value={stats.totalPlaces} />
                <StatCard label="Free places" value={stats.freePlaces} accent="text-green-700" />
                <StatCard label="Occupancy" value={`${occupancyPct}%`} sub={`${stats.occupiedPlaces} / ${stats.totalPlaces}`} accent={occupancyPct > 80 ? "text-orange-600" : "text-gray-800"} />
              </div>
            </section>

            {/* Occupancy bar */}
            <div className="bg-white border border-gray-200 rounded-lg px-5 py-4">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm font-medium text-gray-700">Overall occupancy rate</span>
                <span className="text-sm font-bold text-gray-800">{occupancyPct}%</span>
              </div>
              <div className="h-3 rounded-full bg-gray-100 overflow-hidden">
                <div
                  className={`h-full rounded-full transition-all ${occupancyPct > 80 ? "bg-orange-500" : "bg-green-500"}`}
                  style={{ width: `${occupancyPct}%` }}
                />
              </div>
            </div>

            {/* Room type breakdown */}
            {Object.keys(stats.roomsByType).length > 0 && (
              <section>
                <h2 className="text-sm font-semibold uppercase tracking-wide text-gray-400 mb-3">Rooms by type</h2>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                  {Object.entries(stats.roomsByType).map(([type, count]) => (
                    <StatCard
                      key={type}
                      label={ROOM_TYPE_LABELS[type] ?? type}
                      value={count}
                      sub={`${count} room${count !== 1 ? "s" : ""}`}
                    />
                  ))}
                </div>
              </section>
            )}

            {/* Students section */}
            <section>
              <h2 className="text-sm font-semibold uppercase tracking-wide text-gray-400 mb-3">Students</h2>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                <StatCard label="Active students" value={stats.activeStudents} />
                <StatCard label="Without room" value={stats.studentsWithoutRoom} accent={stats.studentsWithoutRoom > 0 ? "text-yellow-600" : "text-gray-800"} />
                <StatCard label="In arrears" value={stats.studentsInArrears} accent={stats.studentsInArrears > 0 ? "text-red-600" : "text-gray-800"} sub={stats.studentsInArrears > 0 ? `${stats.totalArrears?.toFixed(2)} PLN total` : undefined} />
              </div>
            </section>

            {/* Arrears table */}
            {stats.arrearsDetails.length > 0 && (
              <section>
                <h2 className="text-sm font-semibold uppercase tracking-wide text-gray-400 mb-3">
                  Students in arrears ({stats.arrearsDetails.length})
                </h2>
                <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-gray-100 bg-gray-50 text-left">
                        <th className="px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Student</th>
                        <th className="px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Room</th>
                        <th className="px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Balance</th>
                        <th className="px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Monthly rent</th>
                        <th className="px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Deficit</th>
                      </tr>
                    </thead>
                    <tbody>
                      {stats.arrearsDetails.map((a, i) => (
                        <tr
                          key={a.userId}
                          className={`border-b border-gray-50 ${i % 2 === 1 ? "bg-gray-50/40" : ""}`}
                        >
                          <td className="px-4 py-3">
                            <div className="font-medium text-gray-800">{a.name}</div>
                            <div className="text-xs text-gray-400">{a.email}</div>
                          </td>
                          <td className="px-4 py-3 text-gray-600">{a.roomNumber}</td>
                          <td className="px-4 py-3 text-right font-medium text-gray-700">{a.balance.toFixed(2)} PLN</td>
                          <td className="px-4 py-3 text-right text-gray-600">{a.monthlyRent.toFixed(2)} PLN</td>
                          <td className="px-4 py-3 text-right font-semibold text-red-600">−{a.deficit.toFixed(2)} PLN</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr className="border-t border-gray-200 bg-gray-50">
                        <td colSpan={4} className="px-4 py-3 text-xs font-semibold text-gray-500">Total outstanding debt</td>
                        <td className="px-4 py-3 text-right font-bold text-red-700">−{stats.totalArrears?.toFixed(2)} PLN</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </section>
            )}
          </>
        )}
      </div>
    </div>
  );
}
