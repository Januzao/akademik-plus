export default function ProfilePage() {
  return (
    <div className="bg-[#f0f4f0] min-h-screen px-8 py-8">

      {/* Two column grid — full width with small margin */}
      <div className="grid grid-cols-2 gap-5 w-full max-w-6xl mx-auto">

        {/* Left card */}
        <div className="bg-white rounded-2xl border border-green-100 shadow-sm p-7">

          {/* Card header */}
          <h2 className="text-lg font-semibold text-gray-800">Profile</h2>
          <p className="text-xs text-gray-400 mt-0.5 mb-5">Manage your account information</p>

          {/* Profile photo block */}
          <div className="flex items-center gap-4 mb-6">
            <div className="size-16 rounded-full bg-green-200 flex items-center justify-center text-green-700 font-semibold text-lg shrink-0">
              JS
            </div>
            <div className="flex flex-col gap-0.5">
              <span className="text-sm font-semibold text-gray-800">Profile Photo</span>
              <span className="text-xs text-gray-400">Update your profile picture</span>
              <button className="flex items-center gap-1 text-xs text-gray-400 hover:text-gray-600 mt-1 transition-colors">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5m-13.5-9L12 3m0 0 4.5 4.5M12 3v13.5"/></svg>
                Upload Photo
              </button>
            </div>
          </div>

          {/* Separation line */}
          <div className="h-px bg-green-100 w-full my-4" />

          {/* Basic Information */}
          <h3 className="text-sm font-semibold text-gray-800 mb-4">Basic Information</h3>

          <div className="grid grid-cols-2 gap-x-6 gap-y-5">

            {/* First Name */}
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"/></svg>
                First Name
              </label>
              <input type="text" defaultValue="John" className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200" />
            </div>

            {/* Last Name */}
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"/></svg>
                Last Name
              </label>
              <input type="text" defaultValue="Smith" className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200" />
            </div>

            {/* Room Number */}
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/><path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z"/></svg>
                Room Number
              </label>
              <input type="text" defaultValue="305" className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200" />
            </div>

            {/* Phone Number */}
            <div className="flex flex-col gap-1">
              <label className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 0 0 2.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106c-.44-.11-.902.055-1.173.417l-.97 1.293c-.282.376-.769.542-1.21.38a12.035 12.035 0 0 1-7.143-7.143c-.162-.441.004-.928.38-1.21l1.293-.97c.363-.271.527-.734.417-1.173L6.963 3.102a1.125 1.125 0 0 0-1.091-.852H4.5A2.25 2.25 0 0 0 2.25 4.5v2.25Z"/></svg>
                Phone Number
              </label>
              <input type="text" defaultValue="+48 (234) 567-890" className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200" />
            </div>

            {/* Email Address — spans both columns */}
            <div className="flex flex-col gap-1 col-span-2">
              <label className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 0 1-2.25 2.25h-15a2.25 2.25 0 0 1-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25m19.5 0v.243a2.25 2.25 0 0 1-1.07 1.916l-7.5 4.615a2.25 2.25 0 0 1-2.36 0L3.32 8.91a2.25 2.25 0 0 1-1.07-1.916V6.75"/></svg>
                Email Address
              </label>
              <input type="email" defaultValue="john.smith@example.com" className="bg-green-50 border border-green-100 rounded-lg px-3 py-2 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200" />
            </div>

          </div>

          {/* Separation line */}
          <div className="h-px bg-green-100 w-full my-5" />

          {/* Action buttons */}
          <div className="flex items-center justify-between">
            <button className="px-4 py-2.5 bg-green-500 hover:bg-green-600 text-white text-sm font-medium rounded-lg transition-colors">
              Request repair
            </button>
            <div className="flex items-center gap-3">
              <button className="px-4 py-2.5 border border-gray-200 hover:bg-gray-50 text-gray-700 text-sm font-medium rounded-lg transition-colors">
                Cancel
              </button>
              <button className="px-4 py-2.5 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors">
                Request Changes
              </button>
            </div>
          </div>

        </div>

        {/* Right card — empty for now */}
        <div className="bg-white rounded-2xl border border-green-100 shadow-sm p-7">
        </div>

      </div>
    </div>
  )
}