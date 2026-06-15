"use client";

import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ProfileDetailsCard from "../components/ProfileDetailsCard";
import ProfileEditCard, { type ProfileFormData } from "../components/ProfileEditCard";
import ProfilePaymentCard from "../components/ProfilePaymentCard";
import ProfileHousingHistoryCard from "../components/ProfileHousingHistoryCard";
import ProfileMaintenanceCard from "../components/ProfileMaintenanceCard";
import ProfileBillsCard from "../components/ProfileBillsCard";
import { apiFetch, handleResponse } from "../api/client";
import { useAuth } from "../hooks/AuthContext";

interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  roomNumber: string | null;
  isActive: boolean;
  balance: number | null;
  monthlyRent: number | null;
  profilePhoto: string | null;
}

export default function ProfilePage() {
  const navigate = useNavigate();
  const { updateProfilePhoto } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [user, setUser] = useState<UserProfile | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    apiFetch("/api/auth/me")
      .then(res => handleResponse<UserProfile>(res))
      .then(data => setUser(data))
      .catch(() => {});
  }, []);

  const handleSave = async (data: ProfileFormData) => {
    setSaving(true);
    try {
      const res = await apiFetch("/api/auth/profile", {
        method: "PUT",
        body: JSON.stringify({
          firstName: data.firstName,
          lastName: data.lastName,
          phone: data.phone,
        }),
      });
      const updated = await handleResponse<UserProfile>(res);
      setUser(updated);
      setIsEditing(false);
    } catch {
      // keep editing open on error
    } finally {
      setSaving(false);
    }
  };

  if (!user) {
    return (
      <div className="bg-[#f0f4f0] py-8 flex justify-center">
        <span className="text-gray-400 text-sm">Loading profile...</span>
      </div>
    );
  }

  const formData: ProfileFormData = {
    firstName: user.firstName,
    lastName: user.lastName,
    phone: user.phone ?? "",
    email: user.email,
  };

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="grid grid-cols-2 gap-5 w-full max-w-7xl mx-auto">

        {/* Left column */}
        <div className="flex flex-col gap-5">
          {isEditing ? (
            <ProfileEditCard
              initialData={formData}
              onCancel={() => setIsEditing(false)}
              onSave={handleSave}
              saving={saving}
            />
          ) : (
            <ProfileDetailsCard
              firstName={user.firstName}
              lastName={user.lastName}
              phone={user.phone ?? ""}
              email={user.email}
              roomNumber={user.roomNumber}
              isActive={user.isActive}
              profilePhoto={user.profilePhoto}
              monthlyRent={user.monthlyRent}
              onEdit={() => setIsEditing(true)}
              onRequestRepair={() => navigate("/account/repair")}
              onPhotoUpdated={(url) => {
                setUser(u => u ? { ...u, profilePhoto: url } : u);
                updateProfilePhoto(url);
              }}
            />
          )}

          <ProfileHousingHistoryCard />
          <ProfileMaintenanceCard />
          <ProfileBillsCard />
        </div>

        {/* Right column — payment history */}
        <ProfilePaymentCard
          onMakePayment={() => navigate("/account/payment")}
          balance={user.balance}
        />

      </div>
    </div>
  );
}
