import { createContext, useContext, useState, type ReactNode } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";
import type { LoginResponse, User } from "../api/types";

interface AuthValue {
  user: User | null;
  loginWithGoogle: (idToken: string) => Promise<void>;
  devLogin: () => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();

  const [user, setUser] = useState<User | null>(() => {
    const saved = sessionStorage.getItem("user");
    return saved ? (JSON.parse(saved) as User) : null;
  });

  function saveSession(data: LoginResponse) {
    sessionStorage.setItem("token", data.token);
    sessionStorage.setItem("user", JSON.stringify(data.user));
    setUser(data.user);
  }

  async function loginWithGoogle(idToken: string) {
    const res = await api.post<LoginResponse>("/auth/google", { idToken });
    saveSession(res.data);
  }

  async function devLogin() {
    const res = await api.post("/dev/login");
    saveSession(res.data);
  }

  async function logout() {
    sessionStorage.clear();
    queryClient.clear();
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, loginWithGoogle, devLogin, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return ctx;
}
