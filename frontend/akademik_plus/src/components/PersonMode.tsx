import { useEffect, useMemo, useState } from "react";
import { fetchRooms } from "../api/RoomsApi";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";
import { fetchUsers } from "../api/UsersApi";
import AdminUserEditPanel, { type AdminUserDTO } from "./AdminUserEditPanel";

interface UserResponseDTO {
    id?: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    phone?: string;
    role?: string;
    isActive?: boolean;
    profilePhoto?: string;
    roomId?: number;
}

interface PersonModeProps {
    search: string;
}

function getInitials(firstName?: string, lastname?: string) {
    const first = (firstName ?? "").trim().charAt(0);
    const last = (lastname ?? "").trim().charAt(0);
    return `${first}${last}`.toUpperCase() || "?";
}

export default function PersonMode({ search }: PersonModeProps) {
  const [people, setPeople] = useState<UserResponseDTO[]>([]);
  const [rooms, setRooms] = useState<RoomResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editUser, setEditUser] = useState<AdminUserDTO | null>(null);

  useEffect(() => {
    let active = true;

    Promise.all([fetchUsers(), fetchRooms()])
      .then(([users, roomList]) => {
        if (!active) return;
        setPeople(users as UserResponseDTO[]);
        setRooms(roomList);
      })
      .catch((err) => {
        if (!active) return;
        setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => { active = false; };
  }, []);

  const roomNumberById = useMemo(() => {
    return new Map(
      rooms
        .filter((room) => room.id != null)
        .map((room) => [room.id as number, room.roomNumber ?? String(room.id)])
    );
  }, [rooms]);

  const filteredPeople = useMemo(() => {
    const query = search.trim().toLowerCase();
    return people.filter((person) => {
      const fullName = `${person.firstName ?? ""} ${person.lastName ?? ""}`.trim().toLowerCase();
      const roomNumber = person.roomId ? (roomNumberById.get(person.roomId) ?? "") : "";
      if (query === "") return true;
      return fullName.includes(query) || roomNumber.toLowerCase().includes(query);
    });
  }, [people, roomNumberById, search]);

  const handleSaved = (updated: AdminUserDTO) => {
    setPeople(prev =>
      prev.map(p => p.id === updated.id ? { ...p, roomId: updated.roomId, isActive: updated.isActive } : p)
    );
    setEditUser(null);
  };

  if (loading) {
    return (
      <div className="border border-gray-200 bg-white p-6 text-sm text-gray-500">
        Loading people...
      </div>
    );
  }

  if (error) {
    return (
      <div className="border border-red-200 bg-red-50 p-6 text-sm text-red-700">
        Error: {error}
      </div>
    );
  }

  return (
    <>
      <div className="space-y-4">
        {filteredPeople.length === 0 ? (
          <div className="border border-gray-200 bg-white p-6 text-sm text-gray-500">
            No people match the search.
          </div>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {filteredPeople.map((person) => {
              const roomNumber = person.roomId
                ? roomNumberById.get(person.roomId) ?? "Unknown"
                : "No room";

              return (
                <button
                  key={person.id ?? `${person.firstName}-${person.lastName}`}
                  onClick={() => person.id != null && setEditUser(person as AdminUserDTO)}
                  className="border border-gray-200 bg-white p-5 shadow-sm transition-shadow hover:shadow-md hover:border-green-300 text-left w-full"
                >
                  <div className="flex items-start gap-4">
                    <div className="flex size-12 items-center justify-center rounded-full bg-emerald-100 text-sm font-bold text-emerald-700 shrink-0">
                      {getInitials(person.firstName, person.lastName)}
                    </div>

                    <div className="min-w-0 flex-1">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <h3 className="text-base font-semibold text-gray-900">
                            {person.firstName} {person.lastName}
                          </h3>
                          <p className="text-sm text-gray-500">
                            {person.email || "No email"}
                          </p>
                        </div>

                        <span
                          className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-medium ${
                            person.isActive
                              ? "bg-emerald-50 text-emerald-700"
                              : "bg-red-50 text-red-600"
                          }`}
                        >
                          {person.isActive ? "Active" : "Disabled"}
                        </span>
                      </div>

                      <div className="mt-4 grid grid-cols-2 gap-3 text-sm">
                        <div>
                          <p className="text-gray-400">Room</p>
                          <p className="font-semibold text-gray-900">{roomNumber}</p>
                        </div>
                        <div>
                          <p className="text-gray-400">Role</p>
                          <p className="font-semibold text-gray-900">{person.role || "Student"}</p>
                        </div>
                        <div className="col-span-2">
                          <p className="text-gray-400">Phone</p>
                          <p className="font-semibold text-gray-900">{person.phone || "No phone"}</p>
                        </div>
                      </div>

                      <p className="mt-3 text-xs text-gray-400">Click to edit →</p>
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>

      {editUser && (
        <AdminUserEditPanel
          user={editUser}
          rooms={rooms}
          onClose={() => setEditUser(null)}
          onSaved={handleSaved}
        />
      )}
    </>
  );
}
