export function getRoomStatus(occupancy: number, capacity: number) {
  if (capacity <= 0) return "empty";
  if (occupancy <= 0) return "empty";
  if (occupancy >= capacity) return "full";
  return "partial";
}
 
export const STATUS_STYLES = {
  empty: {
    card: "bg-emerald-100 border-emerald-300 hover:bg-emerald-200/70",
    badge: "bg-white/70 text-emerald-800 border-emerald-300",
    text: "text-emerald-900",
    sub: "text-emerald-700",
  },
  partial: {
    card: "bg-lime-100 border-lime-300 hover:bg-lime-200/70",
    badge: "bg-white/70 text-lime-800 border-lime-300",
    text: "text-lime-900",
    sub: "text-lime-700",
  },
  full: {
    card: "bg-rose-100 border-rose-300 hover:bg-rose-200/70",
    badge: "bg-white/70 text-rose-800 border-rose-300",
    text: "text-rose-900",
    sub: "text-rose-700",
  },
};
