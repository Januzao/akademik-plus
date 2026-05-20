import RoomsByFloor from "../components/RoomsByFloor";

export default function MainPage() {
  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <RoomsByFloor floorNumber={1} />
      </div>
    </div>
  )
}