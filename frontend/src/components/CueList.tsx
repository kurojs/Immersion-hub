import { useEffect, useRef } from "react";
import type { Cue } from "../api/types.ts";

interface Props {
  cues: Cue[];
  activeIndex: number;
  onSelect: (cue: Cue) => void;
}

export default function CueList({ cues, activeIndex, onSelect }: Props) {
  const activeRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    activeRef.current?.scrollIntoView({ behavior: "smooth", block: "center" });
  }, [activeIndex]);

  if (cues.length === 0) {
    return <div className="text-center text-gray-500">No cues available</div>;
  }

  return (
    <div className="max-h-[70vh] space-y-1 overflow-y-auto rounded-xl border border-white/10 p-2">
      {cues.map((cue, i) => (
        <button
          key={cue.id}
          ref={i === activeIndex ? activeRef : null}
          onClick={() => onSelect(cue)}
          className={
            "block w-full rounded-lg p-2 text-left " +
            (i === activeIndex ? "bg-white/20" : "hover:bg-white/10")
          }
        >
          <p>{cue.text}</p>
          {cue.translation && (
            <p className="text-sm text-gray-400">{cue.translation}</p>
          )}
        </button>
      ))}
    </div>
  );
}
