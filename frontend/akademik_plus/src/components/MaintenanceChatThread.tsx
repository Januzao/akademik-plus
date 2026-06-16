import { useEffect, useRef, useState } from "react";

import { fetchMessages, postMessage, type MaintenanceMessageDTO } from "../api/MaintenanceApi";
import { useAuth } from "../hooks/AuthContext";

interface Props {
  requestId: number;
}

function formatTime(raw: string): string {
  return new Date(raw).toLocaleString("en-GB", {
    day: "numeric", month: "short", hour: "2-digit", minute: "2-digit",
  });
}

export default function MaintenanceChatThread({ requestId }: Props) {
  const { user } = useAuth();
  const [messages, setMessages] = useState<MaintenanceMessageDTO[]>([]);
  const [text, setText] = useState("");
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const load = () => {
    fetchMessages(requestId)
      .then(setMessages)
      .catch(() => {});
  };

  useEffect(() => {
    load();
    intervalRef.current = setInterval(load, 3000);
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [requestId]);

  const handleSend = async () => {
    const trimmed = text.trim();
    if (!trimmed || sending) return;
    setSending(true);
    setError(null);
    try {
      const msg = await postMessage(requestId, trimmed);
      setMessages(prev => [...prev, msg]);
      setText("");
    } catch {
      setError("Failed to send message. Please try again.");
    } finally {
      setSending(false);
    }
  };

  const handleKey = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="mt-4 border-t border-gray-100 pt-4">
      <h4 className="text-xs font-semibold uppercase tracking-wide text-gray-400 mb-3 flex items-center gap-1.5">
        <svg className="size-3.5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M8.625 12a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H8.25m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H12m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25Z"/>
        </svg>
        Discussion
      </h4>

      {/* Message list */}
      <div className="flex flex-col gap-2 max-h-64 overflow-y-auto pr-1">
        {messages.length === 0 && (
          <p className="text-xs text-gray-400 text-center py-4">
            No messages yet. Start the conversation.
          </p>
        )}

        {messages.map(msg => {
          const isOwn = user?.email && msg.senderRole === user.role;
          const isAdmin = msg.senderRole === "ADMIN";

          return (
            <div key={msg.id} className={`flex flex-col ${isOwn ? "items-end" : "items-start"}`}>
              <div className={`max-w-[80%] rounded-2xl px-3 py-2 text-sm ${
                isOwn
                  ? "bg-green-600 text-white rounded-br-sm"
                  : isAdmin
                    ? "bg-blue-50 border border-blue-100 text-gray-800 rounded-bl-sm"
                    : "bg-gray-100 text-gray-800 rounded-bl-sm"
              }`}>
                {!isOwn && (
                  <p className={`text-[10px] font-semibold mb-0.5 ${isAdmin ? "text-blue-600" : "text-gray-500"}`}>
                    {msg.senderName} {isAdmin && "· Admin"}
                  </p>
                )}
                <p className="leading-snug whitespace-pre-wrap break-words">{msg.text}</p>
              </div>
              <span className="text-[10px] text-gray-400 mt-0.5 px-1">
                {formatTime(msg.createdAt)}
              </span>
            </div>
          );
        })}
      </div>

      {/* Input */}
      {error && <p className="text-xs text-red-500 mt-2">{error}</p>}
      <div className="flex gap-2 mt-3">
        <textarea
          value={text}
          onChange={e => setText(e.target.value)}
          onKeyDown={handleKey}
          placeholder="Type a message… (Enter to send)"
          rows={1}
          className="flex-1 resize-none rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm text-gray-800 placeholder-gray-400 outline-none focus:border-green-400 focus:ring-1 focus:ring-green-200 transition-colors"
        />
        <button
          onClick={handleSend}
          disabled={sending || !text.trim()}
          className="shrink-0 rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          {sending ? "…" : "Send"}
        </button>
      </div>
      <p className="text-[10px] text-gray-400 mt-1">Shift+Enter for new line</p>
    </div>
  );
}
