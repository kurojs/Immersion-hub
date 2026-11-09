import { useEffect, useState } from "react";
import { Bookmark, X } from "lucide-react";
import { api } from "../api/client";

interface Props {
  word: string;
  rect: DOMRect;
  sentence: string;
  sentenceTranslation: string | null;
  positionMs: number;
  contentId: number;
  targetLang: string;
  onClose: () => void;
}

export default function WordPopup({
  word,
  rect,
  sentence,
  sentenceTranslation,
  positionMs,
  contentId,
  targetLang,
  onClose,
}: Props) {
  const [translation, setTranslation] = useState<string | null>(null);
  const [error, setError] = useState(false);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    let cancelled = false;
    api
      .post<{ translation: string }>("/translate/word", {
        text: word,
        targetLang,
      })
      .then((res) => {
        if (!cancelled) setTranslation(res.data.translation);
      })
      .catch(() => {
        if (!cancelled) setError(true);
      });
    return () => {
      cancelled = true;
    };
  }, [word, targetLang]);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape") onClose();
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  async function save() {
    try {
      await api.post("/words", {
        word,
        translation,
        targetLang,
        sentence,
        sentenceTranslation,
        positionMs,
        contentId,
      });
      setSaved(true);
    } catch {
      setError(true);
    }
  }

  return (
    <div
      className="fixed z-50 w-64 rounded-xl border border-white/20 bg-gray-900/95 p-3 shadow-xl"
      style={{
        top: rect.bottom + 8,
        left: Math.min(rect.left, window.innerWidth - 280),
      }}
    >
      <div className="flex items-start justify-between gap-2">
        <p className="text-xl font-bold">{word}</p>
        <button type="button" onClick={onClose} aria-label="Close">
          <X size={16} />
        </button>
      </div>

      <p className={`mt-1 text-sm ${error ? "text-red-400" : "text-gray-400"}`}>
        {error
          ? "Translation failed"
          : translation === null
            ? "..."
            : translation}
      </p>

      <p className="mt-2 text-sm text-gray-400">{sentence}</p>
      {sentenceTranslation && (
        <p className="text-sm text-gray-400">{sentenceTranslation}</p>
      )}

      <button
        type="button"
        onClick={save}
        disabled={saved || translation === null}
        className="mt-2 flex items-center gap-2 rounded-lg bg-white/15 px-4 py-2 text-sm hover:bg-white/25 disabled:opacity-50"
      >
        <Bookmark size={16} />
        {saved ? "Saved" : "Save"}
      </button>
    </div>
  );
}
