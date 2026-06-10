export const API_BASE = import.meta.env.VITE_API_BASE ?? '';

export function getToken(): string | null {
  return localStorage.getItem('token');
}

export async function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
  const token = getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> ?? {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      const refreshRes = await fetch(`${API_BASE}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });
      if (refreshRes.ok) {
        const { token: newToken } = await refreshRes.json();
        localStorage.setItem('token', newToken);
        const retryHeaders = { ...headers, Authorization: `Bearer ${newToken}` };
        return fetch(`${API_BASE}${path}`, { ...options, headers: retryHeaders });
      }
    }
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    window.location.href = '/login';
  }

  return res;
}

export async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    let message = text;
    try {
      const json = JSON.parse(text);
      message = json.message ?? json.error ?? text;
    } catch { /* not JSON */ }
    throw new Error(`Request failed (${res.status}): ${message || res.statusText}`);
  }
  if (res.status === 204) return null as T;
  return res.json();
}
