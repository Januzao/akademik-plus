import { useNavigate } from "react-router-dom";
import RequestRepair from "../components/RequestRepairCard";

export default function RequestRepairPage() {
  const navigate = useNavigate();
  const goBack = () => navigate("/account");

  return (
    <RequestRepair onCancel={goBack} />
  );
}