import { NavLink, Outlet, useNavigate } from "react-router";

import { Film, Bookmark, Settings as SettingsIcon, LogOut } from "lucide-react";

import { useAuth } from "../context/AuthContext";

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    "flex items-center gap-2 rounded-lg px-3 py-2 " +
    (isActive ? "bg-white/15 text-white" : "text-gray-400 hover:bg-white/10");

  return (
    <div className="flex min-h-screen bg-gray-950 text-white">
      <aside className="flex w-56 flex-col gap-1 border-r border-white/10 p-4">
        <h2 className="mb-4 text-lg font-bold">Immersion Hub</h2>

        <NavLink to="/" className={linkClass}>
          <Film size={18} />
          <span>Library</span>
        </NavLink>

        <NavLink to="/words" className={linkClass}>
          <Bookmark size={18} />
          <span>Words</span>
        </NavLink>

        <NavLink to="/settings" className={linkClass}>
          <SettingsIcon size={18} />
          <span>Settings</span>
        </NavLink>

        <div className="mt-auto space-y-2 border-t border-white/10 pt-4">
          {user?.pictureUrl && (
            <img
              src={user.pictureUrl}
              alt="User profile"
              className="h-10 w-10 rounded-full"
            />
          )}
          <span>{user?.name ?? user?.email}</span>

          <button
            type="button"
            onClick={() => {
              logout();
              navigate("/login");
            }}
            className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-gray-400 hover:bg-white/10"
          >
            <LogOut size={18} />
            <span>Log out</span>
          </button>
        </div>
      </aside>

      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}
