// const API_BASE = import.meta.env?.VITE_API_BASE ?? "http://localhost:8080";
const API_BASE = ""
const ROOMS_URL = `${API_BASE}/api/rooms`;

/**
 * RoomController returns RoomResponseDTO objects. We don't know the exact field
 * names of your DTO, so normalizeRoom() maps several common names to a stable
 * shape the UI uses. Adjust the right-hand side to match your real DTO.
 *
 * UI shape: { id, number, floor, type ('SHARED'|'PRIVATE'), capacity, occupancy }
 */

// Vite dev proxy alternative (vite.config.js) so you can keep API_BASE = "":
export default defineConfig({
  server: { proxy: { "/api": "http://localhost:8080" } }
});

export function normalizeRoom(dto) {
  const capacity =
    dto.capacity ?? dto.maxCapacity ?? dto.totalBeds ?? dto.beds ?? 0;

  const occupancy =
    dto.currentOccupancy ??
    dto.occupancy ??
    dto.occupiedCount ??
    dto.occupiedBeds ??
    (Array.isArray(dto.occupants) ? dto.occupants.length : 0);

  return {
    id: dto.id,
    number: String(dto.roomNumber ?? dto.number ?? dto.name ?? dto.id),
    floor: Number(dto.floor ?? dto.floorNumber ?? dto.level ?? 0),
    type: String(dto.type ?? dto.roomType ?? "").toUpperCase(),
    capacity: Number(capacity),
    occupancy: Number(occupancy),
  };
}

async function handle(res) {
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Request failed (${res.status}): ${text || res.statusText}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

/** GET /api/rooms  -> RoomController.getAll() */
export async function fetchRooms() {
  const res = await fetch(ROOMS_URL);
  const data = await handle(res);
  return (data ?? []).map(normalizeRoom);
}

/** GET /api/rooms/{id} -> RoomController.getById() */
export async function fetchRoom(id) {
  const res = await fetch(`${ROOMS_URL}/${id}`);
  return normalizeRoom(await handle(res));
}

/** POST /api/rooms -> RoomController.create() (expects a RoomRequestDTO body) */
export async function createRoom(roomRequestDTO) {
  const res = await fetch(ROOMS_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(roomRequestDTO),
  });
  return normalizeRoom(await handle(res));
}

/** PUT /api/rooms/{id} -> RoomController.update() */
export async function updateRoom(id, roomRequestDTO) {
  const res = await fetch(`${ROOMS_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(roomRequestDTO),
  });
  return normalizeRoom(await handle(res));
}

/** DELETE /api/rooms/{id} -> RoomController.delete() */
export async function deleteRoom(id) {
  const res = await fetch(`${ROOMS_URL}/${id}`, { method: "DELETE" });
  return handle(res);
}
