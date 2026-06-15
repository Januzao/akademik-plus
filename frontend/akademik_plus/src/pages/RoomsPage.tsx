import { useState } from "react";
import RoomsByFloor from "../components/RoomsByFloor";
import RoomFiltersBar from "../components/RoomFilters";
import PersonMode from "../components/PersonMode";
import RoomDetailPanel from "../components/RoomDetailPanel";
import type { RoomFilters } from "../components/roomFiltersUtils";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

const floorCount = 3;
type ViewMode = "rooms" | "people";

export default function RoomsPage() {
  const [filters, setFilters] = useState<RoomFilters>({
    roomType: "All Types",
    occupancyStatus: "All Rooms",
  });

  const [mode, setMode] = useState<ViewMode>("rooms");
  const [search, setSearch] = useState("");
  const [selectedRoom, setSelectedRoom] = useState<RoomResponseDTO | null>(null);

  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-7xl mx-auto space-y-6">
        <RoomFiltersBar
          filters={filters}
          onChange={setFilters}
          mode={mode}
          search={search}
          onSearchChange={setSearch}
          onModeChange={setMode}
        />

        {mode === "people" ? (
          <PersonMode search={search} />
        ) : (
          Array.from({ length: floorCount }, (_, index) => {
            const floorNumber = index + 1;
            return (
              <div key={floorNumber} className="mb-6">
                <section className="space-y-3">
                  <h2 className="text-2xl md:text-3xl font-extrabold text-gray-900">
                    Floor {floorNumber}
                  </h2>
                  <div className="mt-2 h-px w-full bg-gray-200" />
                  <RoomsByFloor
                    floorNumber={floorNumber}
                    filters={filters}
                    onRoomClick={setSelectedRoom}
                  />
                </section>
              </div>
            );
          })
        )}
      </div>

      {selectedRoom && (
        <RoomDetailPanel
          room={selectedRoom}
          onClose={() => setSelectedRoom(null)}
        />
      )}
    </div>
  );
}