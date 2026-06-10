import { useAuth } from './AuthContext';

export function useCurrentUser() {
  const { user, isAuthenticated, isAdmin } = useAuth();
  return { user, isAuthenticated, isAdmin };
}
