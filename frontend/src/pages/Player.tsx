import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { useParams } from "react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../api/client";
import type { Cue, Content } from "../api/types";
import { findActiveCue } from "../utils/findActiveCue";
import CueList from "../components/CueList";
import ClickableText from "../components/ClickableText";
import WordPopup from "../components/WordPopup";
import { useSettings } from "../context/SettingsContext";

export default function Player() {
  const { id } = useParams();
  const { settings } = useSettings();
  const queryClient = useQueryClient();
  const videoRef = useRef<HTMLVideoElement>(null);
  const [fileUrl, setFileUrl] = useState<string | null>(null);
  const [currentMs, setCurrentMs] = useState(0);
  const [selected, setSelected] = useState<{
    word: string;
    rect: DOMRect;
    sentence: string;
    sentenceTranslation: string | null;
    positionMs: number;
  } | null>(null);
  const lang = settings.nativeLang;

  const { data: content } = useQuery({
    queryKey: ["content", id],
    queryFn: async () => (await api.get<Content>(`/contents/${id}`)).data,
  });

  const { data: cues = [] } = useQuery({
    queryKey: ["cues", id, lang],
    queryFn: async () =>
      (await api.get<Cue[]>(`/contents/${id}/cues`, { params: { lang } })).data,
  });

  const translate = useMutation({
    mutationFn: () =>
      api.post(`/contents/${id}/translate`, null, {
        params: { lang },
        timeout: 120000,
      }),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: ["cues", id, lang] }),
  });

  useEffect(() => {
    return () => {
      if (fileUrl) URL.revokeObjectURL(fileUrl);
    };
  }, [fileUrl]);

  function chooseFile(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setFileUrl(URL.createObjectURL(file));
  }

  const src =
    content?.type === "MEDIA_URL"
      ? content.sourceUrl ?? undefined
      : fileUrl ?? undefined;

  const activeIndex = findActiveCue(cues, currentMs);
  const activeCue = activeIndex !== -1 ? cues[activeIndex] : null;

  const needsTranslation = cues.length > 0 && cues.every((c) => !c.translation);

  function seekToCue(cue: Cue) {
    if (videoRef.current) videoRef.current.currentTime = cue.startMs / 1000;
  }

  function handleWord(word: string, rect: DOMRect) {
    if (!activeCue) return;
    setSelected({
      word,
      rect,
      sentence: activeCue.text,
      sentenceTranslation: activeCue.translation,
      positionMs: activeCue.startMs,
    });
    if (settings.pauseOnWordClick) videoRef.current?.pause();
  }

  return (
    <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <div className="space-y-4">
        <div className="flex items-center justify-between gap-4">
          <h1 className="text-2xl font-bold">{content?.title}</h1>

          {needsTranslation && (
            <button
              type="button"
              onClick={() => translate.mutate()}
              disabled={translate.isPending}
              className="rounded-lg bg-white/15 px-4 py-2 text-sm hover:bg-white/25 disabled:opacity-50"
            >
              {translate.isPending ? "Translating..." : "Translate subtitles"}
            </button>
          )}
        </div>

        {translate.isError && (
          <p className="text-sm text-red-400">Could not translate.</p>
        )}

        {content?.type === "LOCAL_FILE" && !fileUrl && (
          <label className="block rounded-xl border border-dashed border-white/20 p-6 text-center text-gray-400">
            Choose a local video file
            <input
              type="file"
              accept="video/*,audio/*"
              onChange={chooseFile}
              className="mt-3 block w-full"
            />
          </label>
        )}

        <video
          src={src}
          ref={videoRef}
          controls
          className="w-full rounded-xl bg-black"
          onTimeUpdate={(e) => setCurrentMs(e.currentTarget.currentTime * 1000)}
        />

        <div className="rounded-xl bg-white/5 p-4 text-center">
          {activeCue ? (
            <p className="text-2xl">
              <ClickableText text={activeCue.text} onWord={handleWord} />
            </p>
          ) : (
            <p className="text-gray-400">...</p>
          )}
          <p className="text-gray-400">{activeCue?.translation}</p>
        </div>
      </div>

      <CueList cues={cues} activeIndex={activeIndex} onSelect={seekToCue} />

      {selected && (
        <WordPopup
          {...selected}
          contentId={Number(id)}
          targetLang={lang}
          onClose={() => setSelected(null)}
        />
      )}
    </div>
  );
}
