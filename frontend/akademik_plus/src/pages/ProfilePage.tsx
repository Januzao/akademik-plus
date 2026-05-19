"use client";

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ProfileDetailsCard from "../components/ProfileDetailsCard";
import ProfileEditCard from "../components/ProfileEditCard";
import ProfilePaymentCard from "../components/ProfilePaymentCard";

interface ProfileData {
  firstName: string;
  lastName: string;
  roomNumber: string;
  phoneNumber: string;
  email: string;
}

const DEFAULT_DATA: ProfileData = {
  firstName: "John",
  lastName: "Smith",
  roomNumber: "305",
  phoneNumber: "+48 (234) 567-890",
  email: "john.smith@example.com",
};

export default function ProfilePage() {
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);
  const [profileData, setProfileData] = useState<ProfileData>(DEFAULT_DATA);

  const handleSave = (data: ProfileData) => {
    setProfileData(data);
    setIsEditing(false);
  };

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="grid grid-cols-2 gap-5 w-full max-w-7xl mx-auto">

        {/* Left card — toggles between details and edit */}
        {isEditing ? (
          <ProfileEditCard
            initialData={profileData}
            onCancel={() => setIsEditing(false)}
            onSave={handleSave}
          />
        ) : (
          <ProfileDetailsCard 
            onEdit={() => setIsEditing(true)} 
            onRequestRepair={() => navigate("/account/repair")}
          />
        )}

        {/* Right card — payment history (navigates to /account/payment on click) */}
        <ProfilePaymentCard onMakePayment={() => navigate("/account/payment")} />

      </div>
    </div>
  );
}