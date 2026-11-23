#!/usr/bin/env bash
# Disables ONE package for user 0 and records it (restore.sh, disabled.txt, DEBLOAT-LOG.md).
# Usage: ./disable-one.sh <package> "<what this was>"
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_SERIAL:?export ANDROID_SERIAL first (README.md, Connecting)}"; export ANDROID_SERIAL
pkg="$1"; what="$2"

out="$(adb shell pm disable-user --user 0 "$pkg" 2>&1)"
echo "$out"
if ! grep -q "new state: disabled-user" <<<"$out"; then
  echo "NOT RECORDED: $pkg was not disabled" >&2
  exit 1
fi

grep -qx "$pkg" disabled.txt || echo "$pkg" >> disabled.txt
grep -q "restore_pkg $pkg\$" restore.sh || echo "restore_pkg $pkg" >> restore.sh
echo "| \`$pkg\` | $what | \`adb shell pm enable $pkg\` |" >> DEBLOAT-LOG.md
