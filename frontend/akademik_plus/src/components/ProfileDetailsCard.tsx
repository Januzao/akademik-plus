import { useRef, useState } from "react";
import { API_BASE, getToken } from "../api/client";

interface ProfileDetailsCardProps {
  firstName: string;
  lastName: string;
  phone: string;
  email: string;
  roomNumber: string | null;
  isActive?: boolean;
  profilePhoto?: string | null;
  monthlyRent?: number | null;
  onEdit: () => void;
  onRequestRepair: () => void;
  onPhotoUpdated?: (newPhotoUrl: string) => void;
}

export default function ProfileDetailsCard({
  firstName,
  lastName,
  phone,
  email,
  roomNumber,
  isActive,
  profilePhoto,
  monthlyRent,
  onEdit,
  onRequestRepair,
  onPhotoUpdated,
}: ProfileDetailsCardProps) {
  const initials = `${firstName?.[0] ?? ""}${lastName?.[0] ?? ""}`.toUpperCase() || "?";
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!fileInputRef.current) return;
    fileInputRef.current.value = "";
    if (!file) return;

    setUploading(true);
    setUploadError(null);
    try {
      const form = new FormData();
      form.append("file", file);
      const token = getToken();
      const res = await fetch(`${API_BASE}/api/auth/photo`, {
        method: "POST",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
        body: form,
      });
      if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(text || `Upload failed (${res.status})`);
      }
      const updated = await res.json();
      onPhotoUpdated?.(updated.profilePhoto);
    } catch (err) {
      setUploadError(err instanceof Error ? err.message : "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="bg-white border border-green-100 shadow-sm p-7">

      <h2 className="text-lg font-semibold text-gray-800">Profile</h2>
      <p className="text-xs text-gray-400 mt-0.5 mb-5">Manage your account information</p>

      {/* Avatar with upload overlay */}
      <div className="flex items-center gap-4 mb-6">
        <div className="relative shrink-0 group">
          {/* Photo or initials */}
          {profilePhoto ? (
            <img
              src={`${API_BASE}${profilePhoto}`}
              alt="Profile"
              className="size-16 rounded-full object-cover"
            />
          ) : (
            <div className="size-16 rounded-full bg-green-200 flex items-center justify-center text-green-700 font-semibold text-lg">
              {initials}
            </div>
          )}

          {/* Upload overlay */}
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            disabled={uploading}
            className="absolute inset-0 rounded-full bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity disabled:cursor-wait"
            title="Change photo"
          >
            {uploading ? (
              <svg className="size-5 text-white animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4l3-3-3-3v4a8 8 0 100 16v-4l-3 3 3 3v-4a8 8 0 01-8-8z"/>
              </svg>
            ) : (
              <svg className="size-5 text-white" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M6.827 6.175A2.31 2.31 0 0 1 5.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 0 0-1.134-.175 2.31 2.31 0 0 1-1.64-1.055l-.822-1.316a2.192 2.192 0 0 0-1.736-1.039 48.774 48.774 0 0 0-5.232 0 2.192 2.192 0 0 0-1.736 1.039l-.821 1.316Z"/>
                <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 12.75a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0ZM18.75 10.5h.008v.008h-.008V10.5Z"/>
              </svg>
            )}
          </button>

          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp,image/gif"
            className="hidden"
            onChange={handleFileChange}
          />
        </div>

        <div className="flex flex-col gap-1">
          <span className="text-sm font-semibold text-gray-800">Profile Photo</span>
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            disabled={uploading}
            className="text-xs text-green-700 hover:text-green-800 text-left disabled:opacity-50 transition-colors"
          >
            {uploading ? "Uploading…" : "Change photo"}
          </button>
          {isActive !== undefined && (
            <span
              className={`inline-flex w-fit items-center gap-1 rounded-full px-2 py-0.5 text-xs font-medium ${
                isActive ? "bg-emerald-50 text-emerald-700" : "bg-red-50 text-red-600"
              }`}
            >
              <span className={`size-1.5 rounded-full ${isActive ? "bg-emerald-500" : "bg-red-400"}`} />
              {isActive ? "Active" : "Disabled"}
            </span>
          )}
        </div>
      </div>

      {uploadError && (
        <p className="mb-4 text-xs text-red-600 bg-red-50 border border-red-100 rounded-md px-3 py-2">
          {uploadError}
        </p>
      )}

      <div className="h-px bg-green-100 w-full my-4" />

      <h3 className="text-sm font-semibold text-gray-800 mb-4">Basic Information</h3>

      <div className="grid grid-cols-2 gap-x-6 gap-y-5">

        <div className="flex flex-col gap-1">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"/>
            </svg>
            First Name
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700">
            {firstName || "—"}
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"/>
            </svg>
            Last Name
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700">
            {lastName || "—"}
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
              <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z"/>
            </svg>
            Room Number
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700">
            {roomNumber ?? <span className="text-gray-400 italic">Not assigned</span>}
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 0 0 2.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106c-.44-.11-.902.055-1.173.417l-.97 1.293c-.282.376-.769.542-1.21.38a12.035 12.035 0 0 1-7.143-7.143c-.162-.441.004-.928.38-1.21l1.293-.97c.363-.271.527-.734.417-1.173L6.963 3.102a1.125 1.125 0 0 0-1.091-.852H4.5A2.25 2.25 0 0 0 2.25 4.5v2.25Z"/>
            </svg>
            Phone Number
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700">
            {phone || "—"}
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v12m-3-2.818.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
            </svg>
            Monthly Rent
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm font-semibold text-green-700">
            {monthlyRent != null
              ? `${Number(monthlyRent).toFixed(2)} PLN / month`
              : roomNumber != null
              ? <span className="text-gray-400 font-normal italic">Price not set</span>
              : <span className="text-gray-400 font-normal italic">No room assigned</span>}
          </div>
        </div>

        <div className="flex flex-col gap-1 col-span-2">
          <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
            <svg className="size-3.5 shrink-0" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 0 1-2.25 2.25h-15a2.25 2.25 0 0 1-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25m19.5 0v.243a2.25 2.25 0 0 1-1.07 1.916l-7.5 4.615a2.25 2.25 0 0 1-2.36 0L3.32 8.91a2.25 2.25 0 0 1-1.07-1.916V6.75"/>
            </svg>
            Email Address
          </span>
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700">
            {email || "—"}
          </div>
        </div>

      </div>

      <div className="h-px bg-green-100 w-full my-5" />

      <div className="flex items-center justify-between">
        <button
          onClick={onRequestRepair}
          className="px-4 py-2.5 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
        >
          Submit Repair Request
        </button>
        <button
          onClick={onEdit}
          className="flex items-center gap-1.5 px-4 py-2.5 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
        >
          Edit
        </button>
      </div>
    </div>
  );
}
