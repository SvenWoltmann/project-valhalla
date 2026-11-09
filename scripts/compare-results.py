#!/usr/bin/env python3
"""Compares fresh example outputs with the committed ones and reports drift.

    scripts/compare-results.py results/<build>

The reference is the newest results/<build>/ directory in the last commit,
read with git, so a run that overwrote the same build directory still
compares against what the article quotes. Values that change from run to
run are masked before comparing: the warmup rounds of Scalarization, the
depth of a stack overflow, the hash of a value object, the class-pointer
and hash bits of the mark word, identity hashes in warnings, the ranks in
class histograms, and the layouts of JDK-internal classes.

Differences are reported, never fatal: a new early-access build may change
layouts or messages, and the point is to see that before the article goes
stale. On GitHub Actions they appear as warnings and in the job summary.
"""

import difflib
import os
import re
import subprocess
import sys

# Classes whose layouts the article shows; everything else in the layout
# dump depends on which JDK-internal classes happen to load.
LAYOUT_CLASSES = {
    "Point", "ShortPoint", "BytePoint", "Event", "Wrapper", "Box", "Holder",
    "Button", "FieldPacking", "BytePoints", "Wrappers", "Person",
    "java/time/LocalDate", "java/time/LocalTime", "java/time/LocalDateTime",
    "java/lang/Integer", "java/lang/Long",
}


def git(*args):
    return subprocess.run(["git", *args], capture_output=True, text=True,
                          check=True).stdout


def build_key(name):
    m = re.match(r"(\d+)-ea\+(\d+)", name)
    return (int(m.group(1)), int(m.group(2))) if m else (0, 0)


def reference_dir():
    dirs = [line.rstrip("/").split("/")[-1]
            for line in git("ls-tree", "--name-only", "HEAD", "results/").splitlines()]
    return max(dirs, key=build_key) if dirs else None


def layouts(text):
    """Only the blocks of the classes the article shows, without addresses."""
    text = re.sub(r"@0x[0-9a-f]+", "", text)
    text = re.sub(r" \{0x[0-9a-f]+\}", "", text)
    kept = []
    for block in text.split("\n---\n"):
        m = re.search(r"Layout of class (\S+) extends", block)
        if m and m.group(1) in LAYOUT_CLASSES:
            block = block[m.start():]
            kept.append(re.sub(r"Static fields:.*?(?=Instance size)", "", block, flags=re.S))
    kept.sort()
    arrays = sorted(re.findall(r"Flat Type Array: .*?\n(?: - .*\n)+", text))
    return "\n---\n".join(kept) + "\n" + "".join(arrays)


def normalize(path, text):
    name = os.path.basename(path)
    if name == "environment.txt":
        return ""
    if name == "PrintLayouts.txt":
        return layouts(text)
    if name.startswith("Scalarization"):
        text = re.sub(r"(Round [12]: value )[\d.,]+", r"\1<warmup>", text)
    if name == "MarkWord.txt":
        # keep the low 11 bits: lock, self-fwd, age and the Valhalla bits
        text = re.sub(r"^(\S+)\s+([01]+)$",
                      lambda m: "%s %s" % (m.group(1), m.group(2)[-11:].rjust(11, "0")),
                      text, flags=re.M)
    if name == "DeepComparison.txt":
        # the recursion depth at the StackOverflowError depends on the stack
        # size of the machine; keep the exception and its first two frames
        text = "\n".join(text.splitlines()[:3]) + "\n"
    if name == "PixelDemo.txt":
        # a value object's hash starts from the identity hash of its class
        text = re.sub(r"-?\d{5,}", "<hash>", text)
        text = re.sub(r"@[0-9a-f]+$", "@<hash>", text, flags=re.M)
    text = re.sub(r"(module) @[0-9a-f]+", r"\1 @<hash>", text)
    text = re.sub(r"^\s*\d+:(?=\s+\d)", "<rank>:", text, flags=re.M)
    # digit grouping depends on the locale of the machine
    text = re.sub(r"(?<=\d)[.,](?=\d{3}\b)", "", text)
    return text


def files_in_commit(ref):
    prefix = "results/%s/" % ref
    return {p[len(prefix):] for p in git("ls-files", prefix).splitlines()}


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    new_dir = sys.argv[1].rstrip("/")
    ref = reference_dir()
    if ref is None:
        print("No committed results to compare with.")
        return
    new_files = {os.path.relpath(os.path.join(d, f), new_dir)
                 for d, _, fs in os.walk(new_dir) for f in fs if f.endswith(".txt")}
    ref_files = files_in_commit(ref)
    on_actions = os.environ.get("GITHUB_ACTIONS") == "true"
    summary = []
    drift = 0
    for rel in sorted(new_files | ref_files):
        if rel not in ref_files:
            problem = "new output, not in results/%s" % ref
            diff = []
        elif rel not in new_files:
            problem = "missing in this run"
            diff = []
        else:
            old = normalize(rel, git("show", "HEAD:results/%s/%s" % (ref, rel)))
            with open(os.path.join(new_dir, rel), encoding="utf-8") as f:
                new = normalize(rel, f.read())
            if old == new:
                continue
            problem = "differs from results/%s" % ref
            diff = list(difflib.unified_diff(old.splitlines(), new.splitlines(),
                                             "committed", "this run", lineterm="", n=1))
        drift += 1
        print("%s: %s" % (rel, problem))
        for line in diff[:40]:
            print("    " + line)
        if on_actions:
            print("::warning file=%s/%s::%s" % (new_dir, rel, problem))
        summary.append("- `%s`: %s" % (rel, problem))
    if drift == 0:
        print("All outputs match results/%s." % ref)
    if on_actions and os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as f:
            f.write("## Outputs compared with results/%s\n\n" % ref)
            f.write("\n".join(summary) + "\n" if summary else "No differences.\n")


if __name__ == "__main__":
    main()
