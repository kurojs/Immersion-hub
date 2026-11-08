import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router";
import { useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";
import type { Content, ContentType } from "../api/types";

export default function NewContent() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [type, setType] = useState<ContentType>("LOCAL_FILE");
  const [title, setTitle] = useState("");
  const [sourceUrl, setSourceUrl] = useState("");
  const [subtitleFile, setSubtitleFile] = useState<File | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      const res = await api.post<Content>("/contents", {
        type,
        title,
        sourceUrl: type === "MEDIA_URL" ? sourceUrl : null,
      });

      if (subtitleFile) {
        const form = new FormData();
        form.append("file", subtitleFile);
        await api.post(`/contents/${res.data.id}/subtitles`, form);
      }

      await queryClient.invalidateQueries({ queryKey: ["contents"] });
      navigate(`/player/${res.data.id}`);
    } catch {
      setError("Could not create it. Check the data and that the file is .srt or .ass");
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="max-w-lg space-y-4">
      <h1 className="text-2xl font-bold">Add content</h1>

      <label className="block space-y-1">
        <span className="text-sm text-gray-400">Title</span>
        <input
          className="w-full rounded-lg border border-white/10 bg-white/5 p-2"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
      </label>

      <label className="block space-y-1">
        <span className="text-sm text-gray-400">Source</span>
        <select
          className="w-full rounded-lg border border-white/10 bg-white/5 p-2"
          value={type}
          onChange={(e) => setType(e.target.value as ContentType)}
        >
          <option value="LOCAL_FILE">File on my computer</option>
          <option value="MEDIA_URL">Link to mp4/mp3</option>
        </select>
      </label>

      {type === "MEDIA_URL" && (
        <label className="block space-y-1">
          <span className="text-sm text-gray-400">URL</span>
          <input
            className="w-full rounded-lg border border-white/10 bg-white/5 p-2"
            value={sourceUrl}
            onChange={(e) => setSourceUrl(e.target.value)}
            required
          />
        </label>
      )}

      <label className="block space-y-1">
        <span className="text-sm text-gray-400">Subtitles (.srt, .ass)</span>
        <input
          type="file"
          accept=".srt,.ass"
          className="w-full rounded-lg border border-white/10 bg-white/5 p-2"
          onChange={(e) => setSubtitleFile(e.target.files?.[0] ?? null)}
        />
      </label>

      {error && <p className="text-sm text-red-400">{error}</p>}

      <button
        type="submit"
        disabled={saving}
        className="rounded-lg bg-white/15 px-4 py-2 hover:bg-white/25 disabled:opacity-50"
      >
        {saving ? "Saving..." : "Create"}
      </button>
    </form>
  );
}
