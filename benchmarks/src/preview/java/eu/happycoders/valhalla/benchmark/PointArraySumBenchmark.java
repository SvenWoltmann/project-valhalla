package eu.happycoders.valhalla.benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Sums the x coordinates of one million points: as identity records, as value records, and as
 * identity records with a third field.
 *
 * <p>The contrast to {@link ArraySumBenchmark}: {@code ValuePoint(int x, int y)} has 8 bytes of
 * data and, with the null marker, does not fit into the 8 bytes of a nullable flat array element.
 * The JVM stores a {@code ValuePoint[]} as an array of pointers, like an {@code IdentityPoint[]};
 * the value objects on the heap are even larger (24 instead of 16 bytes), because they carry the
 * null marker of their nullable flat layout.
 *
 * <p>{@code WideIdentityPoint(int x, int y, int z)} is the control: an identity record with the
 * same 24 bytes on the heap. If {@code ValuePoint[]} is as fast as {@code WideIdentityPoint[]},
 * its disadvantage against {@code IdentityPoint[]} comes from the object size alone.
 *
 * <p>Compiled with preview features, so it runs only with {@code --enable-preview}. {@code order}
 * and the heap size as in {@link ArraySumBenchmark}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(
    value = 5,
    jvmArgsAppend = {"--enable-preview", "-Xms4g", "-Xmx4g", "-Xmn3g"})
@State(Scope.Benchmark)
public class PointArraySumBenchmark {

  private static final int COUNT = 1_000_000;

  @Param({"sequential", "shuffled"})
  private String order;

  private IdentityPoint[] identityPoints;
  private ValuePoint[] valuePoints;
  private WideIdentityPoint[] wideIdentityPoints;

  @Setup
  public void setUp() {
    identityPoints = new IdentityPoint[COUNT];
    valuePoints = new ValuePoint[COUNT];
    wideIdentityPoints = new WideIdentityPoint[COUNT];
    // One loop per array, so the objects of one array lie next to each other on the heap
    for (int i = 0; i < COUNT; i++) {
      identityPoints[i] = new IdentityPoint(i, -i);
    }
    for (int i = 0; i < COUNT; i++) {
      valuePoints[i] = new ValuePoint(i, -i);
    }
    for (int i = 0; i < COUNT; i++) {
      wideIdentityPoints[i] = new WideIdentityPoint(i, -i, 0);
    }
    if (order.equals("shuffled")) {
      shuffle(identityPoints, new Random(42));
      shuffle(valuePoints, new Random(42));
      shuffle(wideIdentityPoints, new Random(42));
    }
  }

  @Benchmark
  public long sumIdentityPoints() {
    long sum = 0;
    for (IdentityPoint point : identityPoints) {
      sum += point.x();
    }
    return sum;
  }

  @Benchmark
  public long sumValuePoints() {
    long sum = 0;
    for (ValuePoint point : valuePoints) {
      sum += point.x();
    }
    return sum;
  }

  @Benchmark
  public long sumWideIdentityPoints() {
    long sum = 0;
    for (WideIdentityPoint point : wideIdentityPoints) {
      sum += point.x();
    }
    return sum;
  }

  private static <T> void shuffle(T[] array, Random random) {
    for (int i = array.length - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      T tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    }
  }

  record IdentityPoint(int x, int y) {}

  value record ValuePoint(int x, int y) {}

  record WideIdentityPoint(int x, int y, int z) {}
}
