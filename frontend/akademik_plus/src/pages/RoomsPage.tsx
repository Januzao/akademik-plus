import { useState } from "react";
import RoomsByFloor from "../components/RoomsByFloor";
import RoomFiltersBar from "../components/RoomFilters";
import type { RoomFilters } from "../components/roomFiltersUtils";

const floorCount = 3; // Later change to backend call

export default function RoomsPage() {
  const [filters, setFilters] = useState<RoomFilters>({
    roomType: "All Types",
    occupancyStatus: "All Rooms",
  });

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-7xl mx-auto space-y-6">
        <RoomFiltersBar filters={filters} onChange={setFilters} />

        {Array.from({ length: floorCount }, (_, index) => {
          const floorNumber = index + 1;
          return (
            <div key={floorNumber} className="mb-6">
              <section className="space-y-3">
                <h2 className="text-2xl md:text-3xl font-extrabold text-gray-900">
                  Floor {floorNumber}
                </h2>
                <div className="mt-2 h-px w-full bg-gray-200" />
                <RoomsByFloor floorNumber={floorNumber} filters={filters} />
              </section>
            </div>
          );
        })}
      </div>
    </div>
  );
}