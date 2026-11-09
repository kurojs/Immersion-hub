import type { Cue } from "../api/types.ts";

export function findActiveCue(cues: Cue[], timeMs: number): number {
  for (let i = 0; i < cues.length; i++) {
    if (cues[i].startMs <= timeMs && timeMs < cues[i].endMs) {
      return i;
    }
  }
  return -1;
}
