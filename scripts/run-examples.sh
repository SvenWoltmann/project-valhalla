#!/usr/bin/env bash
# Runs every example program and compile-error example with the given JDK
# and writes the outputs to results/<build>/.
# Usage: scripts/run-examples.sh <path-to-jdk-28>
set -uo pipefail

JDK=${1:?usage: scripts/run-examples.sh <path-to-jdk-28>}
JAVA="$JDK/bin/java"
JAVAC="$JDK/bin/javac"
ROOT=$(cd "$(dirname "$0")/.." && pwd)
BUILD=$("$JAVA" -version 2>&1 | sed -n 's/.*(build \([^,)]*\).*/\1/p' | head -1)
OUT="$ROOT/results/$BUILD"
rm -rf "$OUT"
mkdir -p "$OUT"
echo "JDK build: $BUILD" > "$OUT/environment.txt"
"$JAVA" -version >> "$OUT/environment.txt" 2>&1

run() { # <dir> <program> <output-name> [java options...]
  local dir=$1 program=$2 name=$3
  shift 3
  mkdir -p "$OUT/$dir"
  (cd "$ROOT/src/main/java/$dir" && "$JAVA" "$@" "$program.java") \
      > "$OUT/$dir/$name.txt" 2>&1
}

compile_error() { # <dir> <file>
  local dir=$1 file=$2 tmp
  tmp=$(mktemp -d)
  mkdir -p "$OUT/compile-errors/$dir"
  (cd "$ROOT/src/compile-errors/$dir" \
      && "$JAVAC" --release 28 --enable-preview -Xlint:-preview -d "$tmp" "$file.java") \
      > "$OUT/compile-errors/$dir/$file.txt" 2>&1
  rm -rf "$tmp"
}

P=--enable-preview

run motivation IdentitySurprises IdentitySurprises-without-preview
run motivation IdentitySurprises IdentitySurprises-with-preview $P
run motivation WrapperConstructor WrapperConstructor-without-preview
run motivation WrapperConstructor WrapperConstructor-with-preview $P
run jdk HasIdentity HasIdentity $P
run declaring MoneyDemo MoneyDemo $P
for p in PersonComparison FractionDemo NaNValues PixelDemo DeepComparison; do
  run equality $p $p $P
done
for p in IdentityOperations CloneValue; do run restrictions $p $p $P; done
run restrictions WeakRefWarning WeakRefWarning-with-preview $P
run restrictions WeakRefWarning WeakRefWarning-without-preview
for p in Numbers ShapesDemo; do run inheritance $p $p $P; done
run construction UserNameDemo UserNameDemo $P
run construction RecordThisLeak RecordThisLeak-without-preview
run construction RecordThisLeak RecordThisLeak-with-preview $P
run serialization/broken SerializeValues SerializeValues $P
run serialization/proxy SerializeMoney SerializeMoney $P
run reflection ReflectValue ReflectValue $P
run reflection ReflectValue ReflectValue-final-field-mutation $P \
    --enable-final-field-mutation=ALL-UNNAMED
run layout PrintLayouts PrintLayouts $P -Xshare:off \
    -XX:+UnlockDiagnosticVMOptions -XX:+PrintFieldLayout -XX:+PrintFlatArrayLayout
run scalarization Scalarization Scalarization-dontinline $P \
    '-XX:CompileCommand=quiet' '-XX:CompileCommand=dontinline,*::move*'
run scalarization Scalarization Scalarization-interpreter $P -Xint
run scalarization Scalarization Scalarization-no-field-passing $P \
    '-XX:CompileCommand=quiet' '-XX:CompileCommand=dontinline,*::move*' \
    -XX:+UnlockDiagnosticVMOptions -XX:-ValueTypePassFieldsAsArgs \
    -XX:-ValueTypeReturnedAsFields
run header MarkWord MarkWord $P --sun-misc-unsafe-memory-access=allow
run footprint DateArrayFootprint DateArrayFootprint-without-preview
run footprint DateArrayFootprint DateArrayFootprint-with-preview $P
run footprint PointArrayFootprint PointArrayFootprint $P

compile_error declaring Counter
compile_error restrictions SynchronizeOnValue
compile_error restrictions Resource
compile_error inheritance Stack
compile_error construction UserName
(cd "$ROOT/src/main/java/serialization/broken" \
    && "$JAVAC" --release 28 --enable-preview -Xlint:serial,-preview \
        -d "$(mktemp -d)" Money.java) > "$OUT/serialization-lint.txt" 2>&1

# Replace the absolute checkout path in launcher notes and warnings
find "$OUT" -name '*.txt' -exec sed -i.bak "s#$ROOT#.#g" {} +
find "$OUT" -name '*.bak' -delete

echo "Outputs written to $OUT"
