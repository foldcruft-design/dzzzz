#!/usr/bin/env bash
set -euo pipefail

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle is not installed on this runner. Please install Gradle before running this script." >&2
exit 1
