"use client";

import { useState } from "react";
import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { apiFetch, handleResponse } from "../api/client";
import { createPayment } from "../api/PaymentsApi";

interface MakePaymentCardProps {
  onCancel: () => void;
  onSuccess?: () => void;
}

interface MeResponse {
  id: number;
}

const CARD_ELEMENT_OPTIONS = {
  style: {
    base: {
      fontSize: "14px",
      color: "#374151",
      fontFamily: "ui-sans-serif, system-ui, sans-serif",
      "::placeholder": { color: "#9ca3af" },
    },
    invalid: {
      color: "#dc2626",
    },
  },
};

export default function MakePaymentCard({ onCancel, onSuccess }: MakePaymentCardProps) {
  const stripe = useStripe();
  const elements = useElements();

  const [nameOnCard, setNameOnCard] = useState("");
  const [amount, setAmount] = useState("");
  const [paidFor, setPaidFor] = useState("Balance top-up");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handlePay = async () => {
    setError(null);

    const amt = parseFloat(amount);
    if (!amount || isNaN(amt) || amt <= 0) {
      setError("Enter a valid amount greater than 0.");
      return;
    }
    if (!stripe || !elements) {
      setError("Stripe is not loaded yet. Please wait.");
      return;
    }
    const cardElement = elements.getElement(CardElement);
    if (!cardElement) {
      setError("Card field not found.");
      return;
    }

    setLoading(true);
    try {
      const { token, error: stripeError } = await stripe.createToken(cardElement, {
        name: nameOnCard.trim() || undefined,
      });

      if (stripeError) {
        setError(stripeError.message ?? "Card error.");
        return;
      }
      if (!token) {
        setError("Could not tokenize card.");
        return;
      }

      const me = await apiFetch("/api/auth/me").then(r => handleResponse<MeResponse>(r));

      await createPayment({
        userId: me.id,
        paidFor: paidFor.trim() || "Balance top-up",
        stripeToken: token.id,
        amount: amt,
      });

      setSuccess(true);
      setTimeout(() => {
        onSuccess?.();
        onCancel();
      }, 1800);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Payment failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="bg-white border border-green-100 shadow-sm p-7 flex flex-col items-center justify-center gap-4 min-h-64">
        <div className="size-14 rounded-full bg-emerald-100 flex items-center justify-center">
          <svg className="size-7 text-emerald-600" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
          </svg>
        </div>
        <div className="text-center">
          <p className="text-base font-semibold text-gray-900">Payment successful</p>
          <p className="text-sm text-gray-500 mt-1">{parseFloat(amount).toFixed(2)} PLN added to your balance</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7">

      <h2 className="text-lg font-semibold text-gray-800">Make a Payment</h2>
      <p className="text-xs text-gray-400 mt-0.5 mb-5">
        Add funds to your account balance
      </p>

      {/* Amount */}
      <div className="flex flex-col gap-1.5 mb-4">
        <label className="text-sm text-gray-800 font-medium">Amount (PLN)</label>
        <input
          type="number"
          min="0.01"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="0.00"
          className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
        />
      </div>

      {/* Description */}
      <div className="flex flex-col gap-1.5 mb-5">
        <label className="text-sm text-gray-800 font-medium">Description</label>
        <input
          type="text"
          value={paidFor}
          onChange={(e) => setPaidFor(e.target.value)}
          placeholder="Balance top-up"
          className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
        />
      </div>

      <div className="flex items-center gap-4 mb-5">
        <div className="h-px bg-green-100 flex-1" />
        <span className="text-xs text-gray-400 tracking-wider font-medium">CARD DETAILS</span>
        <div className="h-px bg-green-100 flex-1" />
      </div>

      {/* Name on card */}
      <div className="flex flex-col gap-1.5 mb-4">
        <label className="text-sm text-gray-800 font-medium">Name on Card</label>
        <input
          type="text"
          value={nameOnCard}
          onChange={(e) => setNameOnCard(e.target.value)}
          placeholder="John Smith"
          className="w-full bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
        />
      </div>

      {/* Stripe CardElement */}
      <div className="flex flex-col gap-1.5 mb-5">
        <label className="text-sm text-gray-800 font-medium">Card</label>
        <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-3 focus-within:ring-2 focus-within:ring-green-200">
          <CardElement options={CARD_ELEMENT_OPTIONS} />
        </div>
        <p className="text-xs text-gray-400">Card number, expiry and CVV in one secure field</p>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Buttons */}
      <div className="flex items-center justify-end gap-3 mb-5">
        <button
          onClick={onCancel}
          disabled={loading}
          className="px-5 py-2.5 border border-gray-200 hover:bg-gray-50 text-gray-700 text-sm font-medium rounded-lg transition-colors disabled:opacity-50"
        >
          Cancel
        </button>
        <button
          onClick={handlePay}
          disabled={loading || !stripe}
          className="flex items-center gap-1.5 px-5 py-2.5 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-60"
        >
          {loading ? (
            <>
              <svg className="size-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              Processing...
            </>
          ) : (
            `Pay ${amount ? `${parseFloat(amount || "0").toFixed(2)} PLN` : ""}`
          )}
        </button>
      </div>

      <div className="bg-yellow-50 border border-yellow-200 rounded-lg px-4 py-3">
        <p className="text-xs text-gray-700">
          <span className="font-semibold">Note:</span> Card data goes directly to Stripe — it never touches our server.
        </p>
      </div>

    </div>
  );
}
