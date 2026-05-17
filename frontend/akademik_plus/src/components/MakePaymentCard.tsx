"use client";

import { useState } from "react";

interface MakePaymentCardProps {
  onCancel: () => void;
}

export default function MakePaymentCard({ onCancel }: MakePaymentCardProps) {
  const [cardNumber, setCardNumber] = useState("");
  const [nameOnCard, setNameOnCard] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvv, setCvv] = useState("");
  const [amount, setAmount] = useState("850.00");

  const handlePay = () => {
    console.log({ cardNumber, nameOnCard, expiry, cvv, amount });
    onCancel();
  };

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7">

      {/* Header */}
      <h2 className="text-lg font-semibold text-gray-800">Make a Payment</h2>
      <p className="text-xs text-gray-400 mt-0.5 mb-5">
        Choose your preferred payment method to complete your rent payment
      </p>

      {/* Digital Wallets */}
      <h3 className="text-sm font-semibold text-gray-800 mb-3">Digital Wallets</h3>

      <button className="w-full flex items-center justify-center gap-2 bg-black hover:bg-gray-800 text-white text-sm font-medium rounded-lg py-3 mb-3 transition-colors">
        <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 1.5H8.25A2.25 2.25 0 0 0 6 3.75v16.5a2.25 2.25 0 0 0 2.25 2.25h7.5A2.25 2.25 0 0 0 18 20.25V3.75a2.25 2.25 0 0 0-2.25-2.25H13.5m-3 0V3h3V1.5m-3 0h3m-3 18.75h3"/>
        </svg>
        Apple Pay
      </button>

      <button className="w-full flex items-center justify-center gap-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-800 text-sm font-medium rounded-lg py-3 transition-colors">
        <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 1.5H8.25A2.25 2.25 0 0 0 6 3.75v16.5a2.25 2.25 0 0 0 2.25 2.25h7.5A2.25 2.25 0 0 0 18 20.25V3.75a2.25 2.25 0 0 0-2.25-2.25H13.5m-3 0V3h3V1.5m-3 0h3m-3 18.75h3"/>
        </svg>
        Google Pay
      </button>

      {/* "OR PAY WITH CARD" divider */}
      <div className="flex items-center gap-4 my-6">
        <div className="h-px bg-green-100 flex-1" />
        <span className="text-xs text-gray-400 tracking-wider font-medium">OR PAY WITH CARD</span>
        <div className="h-px bg-green-100 flex-1" />
      </div>

      {/* Card Number */}
      <div className="flex flex-col gap-1.5 mb-4">
        <label className="text-sm text-gray-800 font-medium">Card Number</label>
        <div className="relative">
          <svg className="size-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 21Z"/>
          </svg>
          <input
            type="text"
            value={cardNumber}
            onChange={(e) => setCardNumber(e.target.value)}
            placeholder="1234 5678 9012 3456"
            className="w-full bg-green-50 border border-green-100 rounded-lg pl-9 pr-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
          />
        </div>
      </div>

      {/* Name on Card */}
      <div className="flex flex-col gap-1.5 mb-4">
        <label className="text-sm text-gray-800 font-medium">Name on Card</label>
        <div className="relative">
          <svg className="size-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 21Z"/>
          </svg>
          <input
            type="text"
            value={nameOnCard}
            onChange={(e) => setNameOnCard(e.target.value)}
            placeholder="John Smith"
            className="w-full bg-green-50 border border-green-100 rounded-lg pl-9 pr-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
          />
        </div>
      </div>

      {/* Expiry Date & CVV */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="flex flex-col gap-1.5">
          <label className="text-sm text-gray-800 font-medium">Expiry Date</label>
          <input
            type="text"
            value={expiry}
            onChange={(e) => setExpiry(e.target.value)}
            placeholder="MM/YY"
            className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
          />
        </div>
        <div className="flex flex-col gap-1.5">
          <label className="text-sm text-gray-800 font-medium">CVV</label>
          <input
            type="text"
            value={cvv}
            onChange={(e) => setCvv(e.target.value)}
            placeholder="123"
            maxLength={4}
            className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
          />
        </div>
      </div>

      {/* Amount */}
      <div className="flex flex-col gap-1.5 mb-6">
        <label className="text-sm text-gray-800 font-medium">Amount ($)</label>
        <input
          type="text"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="0.00"
          className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
        />
      </div>

      {/* Action buttons */}
      <div className="flex items-center justify-end gap-3 mb-5">
        <button
          onClick={onCancel}
          className="px-5 py-2.5 border border-gray-200 hover:bg-gray-50 text-gray-700 text-sm font-medium rounded-lg transition-colors"
        >
          Cancel
        </button>
        <button
          onClick={handlePay}
          className="flex items-center gap-1.5 px-5 py-2.5 bg-green-600 hover:bg-green-700 active:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
        >
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 21h19.5m-18-18v18m10.5-18v18m6-13.5V21M6.75 6.75h.75m-.75 3h.75m-.75 3h.75m3-6h.75m-.75 3h.75m-.75 3h.75M6.75 21v-3.375c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21M3 3h12m-.75 4.5H21m-3.75 3.75h.008v.008h-.008v-.008Zm0 3h.008v.008h-.008v-.008Zm0 3h.008v.008h-.008v-.008Z"/>
          </svg>
          Pay to Hotel
        </button>
      </div>

      {/* Note */}
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg px-4 py-3">
        <p className="text-xs text-gray-700">
          <span className="font-semibold">Note:</span> All payments are processed securely. Your payment information will be encrypted and protected.
        </p>
      </div>

    </div>
  );
}