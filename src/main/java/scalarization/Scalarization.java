import java.lang.management.ManagementFactory;

value record ValuePoint(int x, int y) {}
record IdentityPoint(int x, int y) {}

static final int CALLS = 10_000_000;

void main() {
  var threads = (com.sun.management.ThreadMXBean)
      ManagementFactory.getThreadMXBean();
  for (int round = 1; round <= 5; round++) {
    long before = threads.getCurrentThreadAllocatedBytes();
    int sum = sumValuePoints();
    long valueBytes = threads.getCurrentThreadAllocatedBytes() - before;

    before = threads.getCurrentThreadAllocatedBytes();
    sum += sumIdentityPoints();
    long identityBytes = threads.getCurrentThreadAllocatedBytes() - before;

    IO.println("Round %d: value %,d bytes, identity %,d bytes (%d)"
        .formatted(round, valueBytes, identityBytes, sum));
  }
}

int sumValuePoints() {
  ValuePoint p = new ValuePoint(0, 0);
  for (int i = 0; i < CALLS; i++) {
    p = moveValue(p, i);
  }
  return p.x() + p.y();
}

ValuePoint moveValue(ValuePoint p, int i) {
  return new ValuePoint(p.x() + (i & 1), p.y() + (i & 2));
}

int sumIdentityPoints() {
  IdentityPoint p = new IdentityPoint(0, 0);
  for (int i = 0; i < CALLS; i++) {
    p = moveIdentity(p, i);
  }
  return p.x() + p.y();
}

IdentityPoint moveIdentity(IdentityPoint p, int i) {
  return new IdentityPoint(p.x() + (i & 1), p.y() + (i & 2));
}
