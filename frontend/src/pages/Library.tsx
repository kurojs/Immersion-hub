import { Link } from "react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Trash2 } from "lucide-react";
import { api } from "../api/client";
import type { Content } from "../api/types";

export default function Library() {
  const queryClient = useQueryClient();

  const {
    data: contents = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["contents"],
    queryFn: async () => (await api.get<Content[]>("/contents")).data,
  });

  const remove = useMutation({
    mutationFn: (id: number) => api.delete(`/contents/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["contents"] }),
  });

  if (isLoading) return <p>Loading...</p>;
  if (isError) return <p>Error loading contents.</p>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Library</h1>
        <Link
          to="/new"
          className="flex items-center gap-2 rounded-lg bg-white/15 px-4 py-2 hover:bg-white/25"
        >
          <Plus size={18} />
          Add
        </Link>
      </div>

      {contents.length === 0 && <p>No contents available. Add some!</p>}

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {contents.map((c) => (
          <div
            key={c.id}
            className="flex items-start justify-between gap-2 rounded-xl border border-white/10 bg-white/5 p-4"
          >
            <div className="min-w-0">
              <Link
                to={`/player/${c.id}`}
                className="block truncate font-medium hover:underline"
              >
                {c.title}
              </Link>
              <p className="text-sm text-gray-400">
                {c.cueCount} lines ·{" "}
                {c.type === "LOCAL_FILE" ? "Local file" : "Link"}
              </p>
            </div>

            <button
              type="button"
              onClick={() => {
                if (window.confirm("Delete this content?")) remove.mutate(c.id);
              }}
              disabled={remove.isPending}
              aria-label={`Delete ${c.title}`}
              className="shrink-0 rounded-lg p-2 text-gray-400 hover:bg-white/10 hover:text-red-400 disabled:opacity-50"
            >
              <Trash2 size={16} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
