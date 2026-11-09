import { createContext, useContext, type ReactNode } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";
import { useAuth } from "./AuthContext";
import type { Settings } from "../api/types";

interface SettingsValue {
  settings: Settings;
  update: (s: Settings) => Promise<void>;
}

const defaults: Settings = { nativeLang: "es", pauseOnWordClick: true };

const SettingsContext = createContext<SettingsValue | null>(null);

export function SettingsProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const { data } = useQuery({
    queryKey: ["settings"],
    queryFn: async () => (await api.get<Settings>("/me/settings")).data,
    enabled: !!user,
  });

  const mutation = useMutation({
    mutationFn: async (s: Settings) =>
      (await api.put<Settings>("/me/settings", s)).data,
    onSuccess: (saved) => queryClient.setQueryData(["settings"], saved),
  });

  async function update(s: Settings) {
    await mutation.mutateAsync(s);
  }

  return (
    <SettingsContext.Provider value={{ settings: data ?? defaults, update }}>
      {children}
    </SettingsContext.Provider>
  );
}

export function useSettings() {
  const ctx = useContext(SettingsContext);
  if (!ctx)
    throw new Error("useSettings must be used within a SettingsProvider");
  return ctx;
}
