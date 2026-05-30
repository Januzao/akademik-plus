"use client";

import { useState } from "react";

interface RequestRepairCardProps {
  onCancel: () => void;
}

export default function RequestFixPage({ onCancel }: RequestRepairCardProps) {
  const [roomNumber, setRoomNumber] = useState("");
  const [issueCategory, setIssueCategory] = useState("");
  const [priority, setPriority] = useState("");
  const [description, setDescription] = useState("");
  const [fileName, setFileName] = useState("No file chosen");

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    setFileName(file ? file.name : "No file chosen");
  };

  const handleSubmit = () => {
    console.log({ roomNumber, issueCategory, priority, description, fileName });
    onCancel()
  };



  return (
        <div className="bg-white border border-green-100 shadow-sm p-8">

          {/* Header */}
          <h1 className="text-2xl font-bold text-gray-900">Request for Fix</h1>
          <p className="text-sm text-gray-500 mt-1 mb-7">
            Submit a repair request for issues in your room or common areas
          </p>

          {/* Room Number */}
          <div className="flex flex-col gap-1.5 mb-5">
            <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
              <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z"/>
              </svg>
              Room Number
            </label>
            <input
              type="text"
              value={roomNumber}
              onChange={(e) => setRoomNumber(e.target.value)}
              placeholder="e.g., 305"
              className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200"
            />
          </div>

          {/* Issue Category */}
          <div className="flex flex-col gap-1.5 mb-5">
            <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
              <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M11.42 15.17 17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 0 0 4.486-6.336l-3.276 3.277a3.004 3.004 0 0 1-2.25-2.25l3.276-3.276a4.5 4.5 0 0 0-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437 1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008Z"/>
              </svg>
              Issue Category
            </label>
            <div className="relative">
              <select
                value={issueCategory}
                onChange={(e) => setIssueCategory(e.target.value)}
                className="w-full appearance-none bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 pr-9 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200"
              >
                <option value="">Select issue type</option>
                <option value="plumbing">Plumbing</option>
                <option value="electrical">Electrical</option>
                <option value="heating">Heating / Cooling</option>
                <option value="appliance">Appliance</option>
                <option value="furniture">Furniture</option>
                <option value="other">Other</option>
              </select>
              <svg className="size-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
              </svg>
            </div>
          </div>

          {/* Priority Level */}
          <div className="flex flex-col gap-1.5 mb-5">
            <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
              <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z"/>
              </svg>
              Priority Level
            </label>
            <div className="relative">
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value)}
                className="w-full appearance-none bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 pr-9 text-sm text-gray-700 outline-none focus:ring-2 focus:ring-green-200"
              >
                <option value="">Select priority</option>
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
                <option value="urgent">Urgent</option>
              </select>
              <svg className="size-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5"/>
              </svg>
            </div>
          </div>

          {/* Description */}
          <div className="flex flex-col gap-1.5 mb-5">
            <label className="text-sm text-gray-800 font-medium">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Please provide a detailed description of the issue..."
              rows={4}
              className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm text-gray-700 placeholder-gray-400 outline-none focus:ring-2 focus:ring-green-200 resize-none"
            />
            <p className="text-xs text-gray-400">
              Be as specific as possible to help us resolve the issue quickly
            </p>
          </div>

          {/* Upload Photo */}
          <div className="flex flex-col gap-1.5 mb-6">
            <label className="flex items-center gap-1.5 text-sm text-gray-800 font-medium">
              <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5m-13.5-9L12 3m0 0 4.5 4.5M12 3v13.5"/>
              </svg>
              Upload Photo (Optional)
            </label>
            <label className="bg-green-50 border border-green-100 rounded-lg px-3 py-2.5 text-sm cursor-pointer hover:bg-green-100 transition-colors flex items-center gap-3">
              <span className="px-2.5 py-1 bg-white border border-gray-200 rounded text-xs font-medium text-gray-700">
                Choose File
              </span>
              <span className="text-gray-500 text-sm">{fileName}</span>
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
            <p className="text-xs text-gray-400">
              Photos help us better understand and prioritize your request
            </p>
          </div>

          {/* Divider */}
          <div className="h-px bg-green-100 w-full mb-5" />

          {/* Action buttons */}
          <div className="flex items-center justify-end gap-3">
            <button onClick={onCancel} className="px-5 py-2.5 border border-gray-200 hover:bg-gray-50 text-gray-700 text-sm font-medium rounded-lg transition-colors">
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              className="flex items-center gap-1.5 px-5 py-2.5 bg-green-600 hover:bg-green-700 active:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
            >
              <svg className="size-4" fill="none" stroke="currentColor" strokeWidth="1.8" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5"/>
              </svg>
              Submit Request
            </button>
          </div>
        </div>
  );
}