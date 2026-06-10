import { NavLink, useNavigate } from 'react-router-dom';
import { Menu, MenuButton, MenuItem, MenuItems } from '@headlessui/react';
import IvysHome from '../assets/IvysHome.png';
import userPlaceholder from '../assets/user.png';
import { useAuth } from '../hooks/AuthContext';

function classNames(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(' ');
}

export default function Topbar() {
  const navigate = useNavigate();
  const { user, isAdmin, isAuthenticated, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  const navigation = isAdmin
    ? [{ name: 'Dashboard', href: '/admin/dashboard' }]
    : [];

  return (
    <nav className="relative bg-green-800/50 after:pointer-events-none after:absolute after:inset-x-0 after:bottom-0 after:h-px after:bg-white/10">
      <div className="mx-auto max-w-7xl px-2 sm:px-6 lg:px-8">
        <div className="relative flex h-16 items-center justify-between">

          {/* Left: Logo */}
          <div className="flex shrink-0 items-center">
            <img
              alt="Ivys Home"
              src={IvysHome}
              className="h-20 w-auto"
            />
          </div>

          {/* Center: Navigation */}
          <div className="absolute inset-x-0 flex justify-center pointer-events-none">
            <div className="flex space-x-4 pointer-events-auto">
              {navigation.map((item) => (
                <NavLink
                  key={item.name}
                  to={item.href}
                  end
                  className={({ isActive }) =>
                    classNames(
                      isActive ? 'bg-gray-950/50 text-white' : 'text-gray-300 hover:bg-white/5 hover:text-white',
                      'rounded-md px-3 py-2 text-sm font-medium',
                    )
                  }
                >
                  {item.name}
                </NavLink>
              ))}
            </div>
          </div>

          {/* Right: Profile menu */}
          <div className="flex items-center">
            {isAuthenticated ? (
              <Menu as="div" className="relative">
                <MenuButton className="relative flex rounded-full focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500">
                  <span className="absolute -inset-1.5" />
                  <span className="sr-only">Open user menu</span>
                  <img
                    alt=""
                    src={userPlaceholder}
                    className="size-11 rounded-full bg-green-800 outline -outline-offset-1 outline-white/10 object-cover"
                  />
                </MenuButton>

                <MenuItems
                  transition
                  className="absolute right-0 z-10 mt-2 w-56 origin-top-right rounded-md bg-gray-800 py-1 outline -outline-offset-1 outline-white/10 transition data-closed:scale-95 data-closed:transform data-closed:opacity-0 data-enter:duration-100 data-enter:ease-out data-leave:duration-75 data-leave:ease-in"
                >
                  {/* User info */}
                  <div className="px-4 py-2 border-b border-white/10">
                    <p className="text-xs text-gray-400 truncate">{user?.email}</p>
                    <p className="text-xs font-medium text-green-400 mt-0.5">
                      {user?.role === 'ADMIN' ? 'Адміністратор' : 'Студент'}
                    </p>
                  </div>

                  <MenuItem>
                    <button
                      onClick={() => navigate('/account')}
                      className="block w-full px-4 py-2 text-left text-sm text-gray-300 data-focus:bg-white/5 data-focus:outline-hidden"
                    >
                      Мій профіль
                    </button>
                  </MenuItem>
                  <MenuItem>
                    <button
                      onClick={handleLogout}
                      className="block w-full px-4 py-2 text-left text-sm text-red-400 data-focus:bg-white/5 data-focus:outline-hidden"
                    >
                      Вийти
                    </button>
                  </MenuItem>
                </MenuItems>
              </Menu>
            ) : (
              <button
                onClick={() => navigate('/login')}
                className="rounded-md bg-green-700 px-4 py-2 text-sm font-medium text-white hover:bg-green-600 transition-colors"
              >
                Увійти
              </button>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
