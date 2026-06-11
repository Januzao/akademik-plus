"use client";

import { useEffect, useMemo, useState } from "react";
import { fetchMyPayments, type PaymentDTO } from "../api/PaymentsApi";

const STATUS_STYLES: Record<string, string> = {
  COMPLETED: "bg-green-400",
  PENDING:   "bg-yellow-400",
  FAILED:    "bg-red-400",
  REFUNDED:  "bg-gray-400",
};

const STATUS_LABELS: Record<string, string> = {
  COMPLETED: "Payment completed",
  PENDING:   "Pending",
  FAILED:    "Payment failed",
  REFUNDED:  "Refunded",
};

function formatDate(raw?: string): string {
  if (!raw) return "";
  return new Date(raw).toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" });
}

interface ProfilePaymentCardProps {
  onMakePayment: () => void;
  balance?: number | null;
}

export default function ProfilePaymentCard({ onMakePayment, balance }: ProfilePaymentCardProps) {
  const [allPayments, setAllPayments] = useState<PaymentDTO[]>([]);
  const [year, setYear] = useState(new Date().getFullYear());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMyPayments()
      .then(data => {
        setAllPayments(data);
        if (data.length > 0) {
          const latestYear = Math.max(...data.map(p => new Date(p.paymentDate).getFullYear()));
          setYear(latestYear);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  const years = useMemo(() => {
    const set = new Set(allPayments.map(p => new Date(p.paymentDate).getFullYear()));
    return Array.from(set).sort((a, b) => a - b);
  }, [allPayments]);

  const minYear = years[0] ?? year;
  const maxYear = years[years.length - 1] ?? year;

  const payments = allPayments.filter(p => new Date(p.paymentDate).getFullYear() === year);
  const total = payments
    .filter(p => p.status === "COMPLETED")
    .reduce((sum, p) => sum + p.amount, 0);

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7 flex flex-col">

      {/* Header */}
      <div className="flex items-center justify-between mb-5">
        <div>
          <h2 className="text-lg font-semibold text-gray-800">Payment History</h2>
          {balance != null && (
            <p className="text-xs text-gray-500 mt-0.5">
              Current balance:{" "}
              <span className={`font-semibold ${balance < 0 ? "text-red-600" : "text-green-700"}`}>
                {balance.toFixed(2)} PLN
              </span>
            </p>
          )}
        </div>
        <button
          onClick={onMakePayment}
          className="flex items-center gap-1.5 px-4 py-2 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
        >
          Make Payment
        </button>
      </div>

      {/* Year navigator */}
      <div className="flex items-center justify-between mb-4">
        <span className="text-xs text-gray-400">Payments for {year}</span>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setYear(y => Math.max(minYear, y - 1))}
            disabled={year <= minYear}
            className="size-6 flex items-center justify-center rounded-md border border-gray-200 text-gray-400 hover:text-gray-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          >
            <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5"/>
            </svg>
          </button>
          <span className="text-sm font-semibold text-gray-700 w-10 text-center">{year}</span>
          <button
            onClick={() => setYear(y => Math.min(maxYear, y + 1))}
            disabled={year >= maxYear}
            className="size-6 flex items-center justify-center rounded-md border border-gray-200 text-gray-400 hover:text-gray-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          >
            <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5"/>
            </svg>
          </button>
        </div>
      </div>

      <div className="h-px bg-green-100 w-full mb-4" />

      {/* Payment list */}
      <div className="flex-1 flex flex-col overflow-y-auto">
        {loading ? (
          <p className="text-sm text-gray-400 text-center py-8">Loading payments…</p>
        ) : payments.length === 0 ? (
          <p className="text-sm text-gray-400 text-center py-8">No payments found for {year}.</p>
        ) : (
          payments.map((p, i) => (
            <div key={p.id}>
              <div className="py-4">
                <div className="flex items-start justify-between mb-1">
                  <span className="text-sm font-bold text-gray-800">{p.amount.toFixed(2)} PLN</span>
                  <span className="text-xs text-gray-400">{formatDate(p.paymentDate)}</span>
                </div>
                <p className="text-xs text-gray-500 mb-2">
                  Payment for: <span className="font-semibold text-gray-700">{p.paidFor}</span>
                </p>
                <div className="flex items-center gap-1.5">
                  <span className={`size-2 rounded-full shrink-0 ${STATUS_STYLES[p.status] ?? "bg-gray-300"}`} />
                  <span className="text-xs text-gray-400">{STATUS_LABELS[p.status] ?? p.status}</span>
                </div>
              </div>
              {i < payments.length - 1 && <div className="h-px bg-gray-100 w-full" />}
            </div>
          ))
        )}
      </div>

      {/* Footer */}
      <div className="h-px bg-green-100 w-full mt-4 mb-4" />
      <div className="flex items-center justify-between">
        <span className="text-xs text-gray-400">Total paid for {year}</span>
        <span className="text-sm font-bold text-gray-800">{total.toLocaleString("pl-PL", { minimumFractionDigits: 2 })} PLN</span>
      </div>
    </div>
  );
}
