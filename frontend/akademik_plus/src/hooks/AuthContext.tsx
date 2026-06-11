import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { API_BASE } from '../api/client';

export type UserRole = 'ADMIN' | 'STUDENT';

export interface CurrentUser {
  email: string;
  role: UserRole;
  profilePhoto: string | null;
}

interface AuthContextType {
  user: CurrentUser | null;
  token: string | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, firstName: string, lastName: string, phone: string) => Promise<void>;
  logout: () => Promise<void>;
  updateProfilePhoto: (url: string) => void;
}

const AuthContext = createContext<AuthContextType>(null!);

function decodeToken(token: string): Omit<CurrentUser, 'profilePhoto'> | null {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return {
      email: payload.sub,
      role: (payload.role as UserRole) ?? 'STUDENT',
    };
  } catch {
    return null;
  }
}

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    if (!payload.exp) return false;
    return Date.now() / 1000 > payload.exp;
  } catch {
    return true;
  }
}

function loadStoredToken(): string | null {
  const t = localStorage.getItem('token');
  if (!t || isTokenExpired(t)) {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    return null;
  }
  return t;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(loadStoredToken);
  const [user, setUser] = useState<CurrentUser | null>(() => {
    const t = loadStoredToken();
    if (!t) return null;
    const decoded = decodeToken(t);
    return decoded ? { ...decoded, profilePhoto: null } : null;
  });

  // Fetch profilePhoto from /api/auth/me whenever token changes
  useEffect(() => {
    if (!token) return;
    fetch(`${API_BASE}/api/auth/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : null)
      .then((data: { profilePhoto?: string | null } | null) => {
        if (data) {
          setUser(u => u ? { ...u, profilePhoto: data.profilePhoto ?? null } : u);
        }
      })
      .catch(() => {});
  }, [token]);

  const storeSession = (accessToken: string, refreshToken: string) => {
    localStorage.setItem('token', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setToken(accessToken);
    const decoded = decodeToken(accessToken);
    setUser(decoded ? { ...decoded, profilePhoto: null } : null);
  };

  const clearSession = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    setToken(null);
    setUser(null);
  };

  const updateProfilePhoto = (url: string) => {
    setUser(u => u ? { ...u, profilePhoto: url } : u);
  };

  const login = async (email: string, password: string) => {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.message ?? 'Невірний email або пароль');
    }
    const data = await res.json();
    storeSession(data.token, data.refreshToken);
  };

  const register = async (email: string, password: string, firstName: string, lastName: string, phone: string) => {
    const res = await fetch(`${API_BASE}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, firstName, lastName, phone }),
    });
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.message ?? 'Реєстрація не вдалась');
    }
    const data = await res.json();
    storeSession(data.token, data.refreshToken);
  };

  const logout = async () => {
    const t = localStorage.getItem('token');
    if (t) {
      await fetch(`${API_BASE}/api/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${t}` },
      }).catch(() => {});
    }
    clearSession();
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      isAuthenticated: !!token,
      isAdmin: user?.role === 'ADMIN',
      login,
      register,
      logout,
      updateProfilePhoto,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
