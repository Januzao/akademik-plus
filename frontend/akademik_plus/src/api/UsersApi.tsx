const API_BASE = import.meta.env?.VITE_API_BASE ?? "";
const USERS_URL = `${API_BASE}/api/users`;

async function handle(res: Response) {
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Request failed (${res.status}): ${text || res.statusText}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export async function fetchUsers() {
  const res = await fetch(USERS_URL);
  const data = await handle(res);
  return data ?? [];
}