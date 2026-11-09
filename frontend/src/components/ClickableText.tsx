const segmenter = new Intl.Segmenter("ja", { granularity: "word" });

interface Props {
  text: string;
  onWord: (word: string, rect: DOMRect) => void;
}

export default function ClickableText({ text, onWord }: Props) {
  const parts = Array.from(segmenter.segment(text));

  return (
    <>
      {parts.map((p, i) =>
        p.isWordLike ? (
          <span
            key={i}
            className="cursor-pointer rounded hover:bg-white/20"
            onClick={(e) =>
              onWord(p.segment, e.currentTarget.getBoundingClientRect())
            }
          >
            {p.segment}
          </span>
        ) : (
          <span key={i}>{p.segment}</span>
        ),
      )}
    </>
  );
}
