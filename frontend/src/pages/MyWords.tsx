import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Trash2 } from "lucide-react";
import { api } from "../api/client";
import type { PageData, SavedWord } from "../api/types";

export default function MyWords() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const size = 20;

  const { data, isLoading, isError } = useQuery({
    queryKey: ["words", page],
    queryFn: async () =>
      (await api.get<PageData<SavedWord>>("/words", { params: { page, size } }))
        .data,
  });

  const remove = useMutation({
    mutationFn: (id: number) => api.delete(`/words/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["words"] }),
  });

  if (isLoading) return <p className="text-gray-400">Loading...</p>;
  if (isError || !data)
    return <p className="text-red-400">Could not load your words.</p>;

  const hasNext = (page + 1) * size < data.totalElements;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">My words ({data.totalElements})</h1>

      {data.content.length === 0 && (
        <p className="text-gray-400">You have not saved any words yet.</p>
      )}

      <div className="grid gap-3 sm:grid-cols-2">
        {data.content.map((w) => (
          <div
            key={w.id}
            className="rounded-xl border border-white/10 bg-white/5 p-4"
          >
            <p className="text-2xl font-bold">{w.word}</p>
            {w.translation && <p className="text-gray-300">{w.translation}</p>}

            {w.sentence && (
              <p className="text-sm text-gray-400">{w.sentence}</p>
            )}
            {w.sentenceTranslation && (
              <p className="text-sm text-gray-400">{w.sentenceTranslation}</p>
            )}

            {w.contentTitle && (
              <p className="text-sm text-gray-500">{w.contentTitle}</p>
            )}

            <button
              type="button"
              onClick={() => remove.mutate(w.id)}
              disabled={remove.isPending}
              className="mt-2 flex items-center gap-1 rounded-lg bg-white/10 px-3 py-1 text-sm hover:bg-white/20 disabled:opacity-50"
            >
              <Trash2 size={16} />
              Delete
            </button>
          </div>
        ))}
      </div>

      <div className="flex items-center justify-center gap-4">
        <button
          type="button"
          onClick={() => setPage(page - 1)}
          disabled={page === 0}
          className="rounded-lg bg-white/10 px-3 py-1 text-sm hover:bg-white/20 disabled:opacity-50"
        >
          Previous
        </button>
        <span className="text-sm">Page {page + 1}</span>
        <button
          type="button"
          onClick={() => setPage(page + 1)}
          disabled={!hasNext}
          className="rounded-lg bg-white/10 px-3 py-1 text-sm hover:bg-white/20 disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
}
