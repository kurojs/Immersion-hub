import { useSettings } from "../context/SettingsContext";

export default function Settings() {
  const { settings, update } = useSettings();

  return (
    <div className="max-w-lg space-y-6">
      <h1 className="text-2xl font-bold">Settings</h1>

      <label className="block space-y-1">
        <span className="text-sm text-gray-400">Native language</span>
        <select
          className="w-full rounded-lg border border-white/10 bg-white/5 p-2"
          value={settings.nativeLang}
          onChange={(e) => update({ ...settings, nativeLang: e.target.value })}
        >
          <option value="es">Spanish</option>
          <option value="en">English</option>
          <option value="pt">Portuguese</option>
          <option value="fr">French</option>
        </select>
      </label>

      <label className="flex items-center gap-2">
        <input
          type="checkbox"
          checked={settings.pauseOnWordClick}
          onChange={(e) =>
            update({ ...settings, pauseOnWordClick: e.target.checked })
          }
        />
        <span>Pause the video when clicking a word</span>
      </label>

      <p className="text-sm text-gray-500">Saves automatically</p>
    </div>
  );
}
