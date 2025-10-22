const listeners = new Set();

export function onUnreadChanged(cb) {
  listeners.add(cb);
  return () => listeners.delete(cb);
}

export function emitUnreadChanged() {
  for (const l of Array.from(listeners)) {
    try { l(); } catch { }
  }
}
