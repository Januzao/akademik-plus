import RoomsByFloor from "../components/RoomsByFloor"

export default function RoomsPage() {
  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-2xl mx-auto">
        <RoomsByFloor floorNumber={1} />
      </div>
    </div>
  );
}