export type ContentType = "LOCAL_FILE" | "MEDIA_URL";

export interface User {
  id: number;
  email: string;
  name: string | null;
  pictureUrl: string | null;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface Content {
  id: number;
  type: ContentType;
  title: string;
  sourceUrl: string | null;
  createdAt: string;
  cueCount: number;
}

export interface Cue {
  id: number;
  orderIndex: number;
  startMs: number;
  endMs: number;
  text: string;
  translation: string | null;
}

export interface SavedWord {
  id: number;
  word: string;
  translation: string | null;
  targetLang: string;
  sentence: string | null;
  sentenceTranslation: string | null;
  positionMs: number | null;
  contentId: number | null;
  contentTitle: string | null;
  createdAt: string;
}

export interface PageData<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}

export interface Settings {
  nativeLang: string;
  pauseOnWordClick: boolean;
}
