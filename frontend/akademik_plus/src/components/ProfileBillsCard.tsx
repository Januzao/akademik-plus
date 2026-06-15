import { useEffect, useState } from "react";
import { fetchMyBills, payBill, type BillDTO } from "../api/BillsApi";

const STATUS_STYLES: Record<string, string> = {
  PENDING:   "bg-yellow-100 text-yellow-700 border-yellow-200",
  PAID:      "bg-emerald-100 text-emerald-700 border-emerald-200",
  CANCELLED: "bg-gray-100 text-gray-500 border-gray-200",
};

const STATUS_LABELS: Record<string, string> = {
  PENDING:   "Pending",
  PAID:      "Paid",
  CANCELLED: "Cancelled",
};

function formatDate(raw?: string | null): string {
  if (!raw) return "";
  return new Date(raw).toLocaleDateString("uk-UA", { day: "2-digit", month: "short", year: "numeric" });
}

export default function ProfileBillsCard() {
  const [bills, setBills] = useState<BillDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [paying, setPaying] = useState<number | null>(null);
  const [payError, setPayError] = useState<{ id: number; message: string } | null>(null);
  const [justPaid, setJustPaid] = useState<number | null>(null);

  useEffect(() => {
    fetchMyBills()
      .then(setBills)
      .catch(err => setError(err instanceof Error ? err.message : "Failed to load bills"))
      .finally(() => setLoading(false));
  }, []);

  const handlePay = async (bill: BillDTO) => {
    setPaying(bill.id);
    setPayError(null);
    try {
      const updated = await payBill(bill.id);
      setBills(prev => prev.map(b => b.id === updated.id ? updated : b));
      setJustPaid(updated.id);
      setTimeout(() => setJustPaid(null), 3000);
    } catch (err) {
      setPayError({ id: bill.id, message: err instanceof Error ? err.message : "Payment failed." });
    } finally {
      setPaying(null);
    }
  };

  const pending = bills.filter(b => b.status === "PENDING");
  const rest    = bills.filter(b => b.status !== "PENDING");
  const sorted  = [...pending, ...rest];

  const totalPending = pending.reduce((sum, b) => sum + b.amount, 0);

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7 flex flex-col gap-5">

      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-gray-800">My Bills</h2>
          {pending.length > 0 && (
            <p className="text-xs text-gray-500 mt-0.5">
              Outstanding:{" "}
              <span className="font-semibold text-yellow-600">
                {totalPending.toFixed(2)} PLN
              </span>
              {" "}({pending.length} unpaid)
            </p>
          )}
        </div>
        <svg className="size-6 text-green-600" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 14.25l6-6m4.5-3.493V21.75l-3.75-1.5-3.75 1.5-3.75-1.5-3.75 1.5V4.757c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0 1 11.186 0c1.1.128 1.907 1.077 1.907 2.185Z" />
        </svg>
      </div>

      <div className="h-px bg-green-100 w-full" />

      {loading ? (
        <p className="text-sm text-gray-400 text-center py-4">Loading bills...</p>
      ) : error ? (
        <p className="text-sm text-red-500 text-center py-4">{error}</p>
      ) : sorted.length === 0 ? (
        <p className="text-sm text-gray-400 text-center py-4">No bills issued yet.</p>
      ) : (
        <div className="space-y-3">
          {sorted.map(bill => (
            <div
              key={bill.id}
              className={`rounded-lg border px-4 py-3 transition-colors ${
                justPaid === bill.id
                  ? "border-emerald-300 bg-emerald-50"
                  : bill.status === "PENDING"
                  ? "border-yellow-200 bg-yellow-50"
                  : "border-gray-100 bg-white"
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-gray-900">{bill.title}</p>
                  {bill.description && (
                    <p className="text-xs text-gray-500 mt-0.5">{bill.description}</p>
                  )}
                  <div className="mt-1.5 flex flex-wrap gap-3 text-xs text-gray-400">
                    <span>Issued: {formatDate(bill.issuedDate)}</span>
                    <span>Due: {formatDate(bill.dueDate)}</span>
                    {bill.paidDate && <span>Paid: {formatDate(bill.paidDate)}</span>}
                  </div>
                  <p className="mt-1 text-xs text-gray-400">
                    Issued by: <span className="text-gray-600">{bill.issuedByName}</span>
                  </p>
                </div>

                <div className="flex flex-col items-end gap-2 shrink-0">
                  <span className="text-base font-bold text-gray-900">
                    {bill.amount.toFixed(2)} PLN
                  </span>
                  <span className={`rounded-full border px-2.5 py-0.5 text-xs font-medium ${STATUS_STYLES[bill.status] ?? "bg-gray-100 text-gray-500 border-gray-200"}`}>
                    {STATUS_LABELS[bill.status] ?? bill.status}
                  </span>
                </div>
              </div>

              {bill.status === "PENDING" && (
                <div className="mt-3 space-y-2">
                  {payError?.id === bill.id && (
                    <p className="text-xs text-red-600">{payError.message}</p>
                  )}
                  <button
                    onClick={() => handlePay(bill)}
                    disabled={paying === bill.id}
                    className="w-full rounded-lg bg-green-700 py-2 text-sm font-medium text-white hover:bg-green-800 transition-colors disabled:opacity-60"
                  >
                    {paying === bill.id ? (
                      <span className="flex items-center justify-center gap-2">
                        <svg className="size-4 animate-spin" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
                        </svg>
                        Processing...
                      </span>
                    ) : (
                      `Pay ${bill.amount.toFixed(2)} PLN from balance`
                    )}
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
