"use client";

import { useState } from "react";

interface Payment {
  amount: number;
  date: string;
  paidFor: string;
}

const paymentsData: Record<number, Payment[]> = {
  2025: [
    { amount: 850, date: "December 14, 2025", paidFor: "January 2026" },
    { amount: 850, date: "November 12, 2025", paidFor: "December 2025" },
    { amount: 850, date: "October 10, 2025",  paidFor: "November 2025" },
  ],
  2026: [
    { amount: 850, date: "February 15, 2026", paidFor: "March 2026" },
    { amount: 850, date: "January 12, 2026",  paidFor: "February 2026" },
  ],
};

const MIN_YEAR = 2025;
const MAX_YEAR = 2026;

interface ProfilePaymentCardProps {
  onMakePayment: () => void;
}

export default function ProfilePaymentCard({ onMakePayment }: ProfilePaymentCardProps) {
  const [year, setYear] = useState(2026);

  const payments = paymentsData[year] ?? [];
  const total = payments.reduce((sum, p) => sum + p.amount, 0);

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7 flex flex-col">

      {/* Card header */}
      <div className="flex items-center justify-between mb-5">
        <h2 className="text-lg font-semibold text-gray-800">Payment History</h2>
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
            onClick={() => setYear((y) => Math.max(MIN_YEAR, y - 1))}
            disabled={year <= MIN_YEAR}
            className="size-6 flex items-center justify-center rounded-md border border-gray-200 text-gray-400 hover:text-gray-600 hover:border-gray-300 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          >
            <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5"/>
            </svg>
          </button>
          <span className="text-sm font-semibold text-gray-700 w-10 text-center">{year}</span>
          <button
            onClick={() => setYear((y) => Math.min(MAX_YEAR, y + 1))}
            disabled={year >= MAX_YEAR}
            className="size-6 flex items-center justify-center rounded-md border border-gray-200 text-gray-400 hover:text-gray-600 hover:border-gray-300 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
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
        {payments.length === 0 ? (
          <p className="text-sm text-gray-400 text-center py-8">No payments found for {year}.</p>
        ) : (
          payments.map((p, i) => (
            <div key={i}>
              <div className="py-4">
                <div className="flex items-start justify-between mb-1">
                  <span className="text-sm font-bold text-gray-800">${p.amount.toFixed(2)}</span>
                  <span className="text-xs text-gray-400">{p.date}</span>
                </div>
                <p className="text-xs text-gray-500 mb-2">
                  Payment for: <span className="font-semibold text-gray-700">{p.paidFor}</span>
                </p>
                <div className="flex items-center gap-1.5">
                  <span className="size-2 rounded-full bg-green-400 shrink-0" />
                  <span className="text-xs text-gray-400">Payment completed</span>
                </div>
              </div>
              {i < payments.length - 1 && <div className="h-px bg-gray-100 w-full" />}
            </div>
          ))
        )}
      </div>

      {/* Footer total */}
      <div className="h-px bg-green-100 w-full mt-4 mb-4" />
      <div className="flex items-center justify-between">
        <span className="text-xs text-gray-400">Total paid for {year}</span>
        <span className="text-sm font-bold text-gray-800">${total.toLocaleString()}.00</span>
      </div>

    </div>
  );
}