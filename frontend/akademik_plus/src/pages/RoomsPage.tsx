import { useNavigate } from "react-router-dom";

export default function RoomsPage() {
  const navigate = useNavigate();
  const goBack = () => navigate("/account");

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-2xl mx-auto">
        
        <RoomsPage onCancel={goBack} />
      </div>
    </div>
  );
}