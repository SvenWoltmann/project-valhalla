#!/usr/bin/env bash
# Builds the JMH benchmarks in benchmarks/ with the given JDK, runs them with
# and without preview features, and writes the results to
# results/<build>/jmh/.
# Usage: scripts/run-benchmarks.sh <path-to-jdk-28>
#
# Takes about 18 minutes. Run it only on an otherwise idle machine: every
# other process that wants CPU time or memory bandwidth shows up in the
# numbers.
set -euo pipefail

JDK=${1:?usage: scripts/run-benchmarks.sh <path-to-jdk-28>}
JAVA="$JDK/bin/java"
ROOT=$(cd "$(dirname "$0")/.." && pwd)
BUILD=$("$JAVA" -version 2>&1 | sed -n 's/.*(build \([^,)]*\).*/\1/p' | head -1)
OUT="$ROOT/results/$BUILD/jmh"

if pgrep -x java > /dev/null; then
  echo "Another JVM is running; stop it first:" >&2
  pgrep -lx java >&2
  exit 1
fi

(cd "$ROOT/benchmarks" \
    && JAVA_HOME="$JDK" mvn -q -B clean package \
    && JAVA_HOME="$JDK" mvn -q -B package -P preview)

rm -rf "$OUT"
mkdir -p "$OUT"
{
  echo "JDK build: $BUILD"
  echo "Date: $(date '+%Y-%m-%d %H:%M %Z')"
  if [[ $(uname) == Darwin ]]; then
    echo "Machine: $(sysctl -n machdep.cpu.brand_string)," \
        "$(sysctl -n hw.ncpu) cores, $(( $(sysctl -n hw.memsize) / 1024 / 1024 / 1024 )) GB RAM"
    echo "OS: $(sw_vers -productName) $(sw_vers -productVersion) ($(uname -m))"
  else
    echo "Machine: $(grep -m1 'model name' /proc/cpuinfo | cut -d: -f2 | xargs)," \
        "$(nproc) cores, $(free -g | awk '/Mem:/ {print $2}') GB RAM"
    echo "OS: $(uname -sr) ($(uname -m))"
  fi
  echo
  "$JAVA" -version 2>&1
} > "$OUT/environment.txt"

# The forks inherit the options of the JMH runner JVM, so --enable-preview
# goes to the runner. The heap options are in the @Fork annotations.
run() { # <name> <jar> [runner options...]
  local name=$1 jar=$2
  shift 2
  "$JAVA" "$@" -jar "$ROOT/benchmarks/target/$jar" \
      -rf json -rff "$OUT/$name.json" -o "$OUT/$name.log"
}

run array-sum-without-preview benchmarks.jar
run array-sum-with-preview benchmarks.jar --enable-preview
run point-array-sum benchmarks-preview.jar --enable-preview

# Check that each run had the preview setting its name says
grep -q -- '--enable-preview' "$OUT/array-sum-without-preview.log" \
    && { echo "array-sum-without-preview ran with preview features" >&2; exit 1; }
grep -q '^# VM options: .*--enable-preview' "$OUT/array-sum-with-preview.log" \
    || { echo "array-sum-with-preview ran without preview features" >&2; exit 1; }

# Replace the absolute checkout path in the JMH warnings
sed -i.bak "s#$ROOT#.#g" "$OUT"/*.log
rm "$OUT"/*.bak

python3 "$ROOT/scripts/summarize-benchmarks.py" "$OUT" > "$OUT/summary.md"
cat "$OUT/summary.md"
echo "Results written to $OUT"
