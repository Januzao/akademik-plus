import { useNavigate } from "react-router-dom";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import MakePaymentCard from "../components/MakePaymentCard";

const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY as string);

export default function MakePaymentPage() {
  const navigate = useNavigate();
  const goBack = () => navigate("/account");

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-2xl mx-auto">

        <button
          onClick={goBack}
          className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 mb-6 transition-colors"
        >
          <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18"/>
          </svg>
          Back to Profile
        </button>

        <Elements stripe={stripePromise}>
          <MakePaymentCard onCancel={goBack} />
        </Elements>
      </div>
    </div>
  );
}
