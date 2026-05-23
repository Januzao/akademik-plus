import RoomsByFloor from "../components/RoomsByFloor"
const floorCount = 5;
export default function RoomsPage() {
  return (
    <div className="bg-[#f0f4f0] py-8">
      <div className="w-full max-w-7xl mx-auto">
        {Array.from({ length: floorCount }, (_, index) => {
          const floorNumber = index + 1;
          return (
            <section key={floorNumber}>
              <h2>Floor {floorNumber}</h2>
              <RoomsByFloor floorNumber={floorNumber} />
            </section>
          );
        })}
      </div>
    </div>
  );
}