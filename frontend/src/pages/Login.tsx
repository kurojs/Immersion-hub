import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router";
import { GoogleLogin } from "@react-oauth/google";

export default function Login() {
  const { loginWithGoogle, devLogin } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState("");

  async function handleDevLogin() {
    try {
      await devLogin();
      navigate("/");
    } catch {
      setError("Dev login failed. Is the backend running?");
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-950 text-white">
      <div className="w-80 space-y-4 rounded-2xl border border-white/10 bg-white/5 p-8 text-center">
        <h1 className="text-2xl font-bold">Immersion Hub</h1>
        <p className="text-sm text-gray-400">Learn Japanese</p>

        <div className="flex justify-center">
          <GoogleLogin
            onSuccess={async (res) => {
              try {
                await loginWithGoogle(res.credential!);
                navigate("/");
              } catch {
                setError("Error logging in with Google. Please try again.");
              }
            }}
            onError={() => setError("Google login failed.")}
          />
        </div>

        {import.meta.env.DEV && (
          <button
            type="button"
            onClick={handleDevLogin}
            className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-gray-300 transition hover:bg-white/10"
          >
            Enter as Dev (no Google login)
          </button>
        )}

        {error && <p className="text-sm text-red-400">{error}</p>}
      </div>
    </div>
  );
}
