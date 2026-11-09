#!/usr/bin/env python3
"""Prints the JMH results of scripts/run-benchmarks.sh as Markdown tables.

    scripts/summarize-benchmarks.py results/<build>/jmh

Each cell is the mean over all measurement iterations of all forks and the
99.9% confidence interval that JMH reports as the error.
"""

import json
import os
import sys

ARRAYS = [
    ("sumInts", "`int[]`"),
    ("sumIntegers", "`Integer[]`"),
    ("sumYears", "`LocalDate[]` (years)"),
]
POINTS = [
    ("sumIdentityPoints", "`IdentityPoint[]` (identity record)"),
    ("sumValuePoints", "`ValuePoint[]` (value record)"),
    ("sumWideIdentityPoints", "`WideIdentityPoint[]` (identity record, 24 bytes)"),
]
ORDERS = ["sequential", "shuffled"]


def load(path):
    with open(path, encoding="utf-8") as f:
        results = {}
        for r in json.load(f):
            method = r["benchmark"].rsplit(".", 1)[1]
            metric = r["primaryMetric"]
            results[(method, r["params"]["order"])] = (
                metric["score"], metric["scoreError"], metric["scoreUnit"],
                r["forks"] * r["measurementIterations"])
    return results


def cell(result):
    score, error, unit, _ = result
    return "%.1f ± %.1f %s" % (score, error, unit)


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    out = sys.argv[1]
    without = load(os.path.join(out, "array-sum-without-preview.json"))
    with_ = load(os.path.join(out, "array-sum-with-preview.json"))
    points = load(os.path.join(out, "point-array-sum.json"))
    n = next(iter(without.values()))[3]

    print("## Sum over one million elements (%d measurements each)\n" % n)
    print("| Array | Order | Without preview | With preview | Speedup |")
    print("|---|---|--:|--:|--:|")
    for method, label in ARRAYS:
        for order in ORDERS:
            a, b = without[(method, order)], with_[(method, order)]
            print("| %s | %s | %s | %s | %.1f× |"
                  % (label, order, cell(a), cell(b), a[0] / b[0]))

    print("\n## Value record that is not flattened (with preview)\n")
    print("| Array | Order | Time |")
    print("|---|---|--:|")
    for method, label in POINTS:
        for order in ORDERS:
            print("| %s | %s | %s |" % (label, order, cell(points[(method, order)])))


if __name__ == "__main__":
    main()
