import { apiFetch, handleResponse } from './client';

export async function fetchUsers() {
  const res = await apiFetch('/api/users');
  return handleResponse<unknown[]>(res).then(data => data ?? []);
}

export async function fetchUserById(id: number) {
  const res = await apiFetch(`/api/users/${id}`);
  return handleResponse<unknown>(res);
}

export async function updateUser(id: number, body: Record<string, unknown>) {
  const res = await apiFetch(`/api/users/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
  return handleResponse<unknown>(res);
}

export async function patchUser(id: number, body: { roomId: number | null; isActive: boolean }) {
  const res = await apiFetch(`/api/users/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(body),
  });
  return handleResponse<unknown>(res);
}

export async function deleteUser(id: number) {
  const res = await apiFetch(`/api/users/${id}`, { method: 'DELETE' });
  return handleResponse<null>(res);
}
