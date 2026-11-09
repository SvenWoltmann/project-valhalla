package eu.happycoders.valhalla.benchmark;

import java.time.LocalDate;
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
 * Sums one million elements of an {@code int[]}, an {@code Integer[]} and a {@code LocalDate[]}
 * (the years of the dates).
 *
 * <p>The class is compiled without preview features, so it runs with and without {@code
 * --enable-preview}. Without preview, {@code Integer[]} and {@code LocalDate[]} hold pointers to
 * objects on the heap; with preview, {@code Integer} and {@code LocalDate} are value classes, and
 * the JVM stores the values themselves in the arrays (8 bytes per element, including the null
 * marker).
 *
 * <p>{@code order} controls where the objects lie on the heap. With {@code sequential}, element
 * {@code i} points to the {@code i}-th object allocated - the objects lie next to each other in
 * array order, the best case for the CPU's prefetcher. With {@code shuffled}, the array elements
 * are shuffled after the allocation, so the loop jumps across the heap, as it does for objects
 * that a long-running application created at different times. A flat array copies the values,
 * so for it the order makes no difference; the same holds for {@code int[]}.
 *
 * <p>The forks run with a heap large enough that no garbage collection happens after the setup;
 * a collection would copy the objects in array order and undo the shuffling.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(
    value = 5,
    jvmArgsAppend = {"-Xms4g", "-Xmx4g", "-Xmn3g"})
@State(Scope.Benchmark)
public class ArraySumBenchmark {

  private static final int COUNT = 1_000_000;

  @Param({"sequential", "shuffled"})
  private String order;

  private int[] ints;
  private Integer[] integers;
  private LocalDate[] dates;

  @Setup
  public void setUp() {
    ints = new int[COUNT];
    integers = new Integer[COUNT];
    dates = new LocalDate[COUNT];
    // One loop per array, so the objects of one array lie next to each other on the heap
    for (int i = 0; i < COUNT; i++) {
      ints[i] = i;
    }
    for (int i = 0; i < COUNT; i++) {
      integers[i] = i;
    }
    LocalDate start = LocalDate.of(2000, 1, 1);
    for (int i = 0; i < COUNT; i++) {
      dates[i] = start.plusDays(i);
    }
    if (order.equals("shuffled")) {
      shuffle(ints, new Random(42));
      shuffle(integers, new Random(42));
      shuffle(dates, new Random(42));
    }
  }

  @Benchmark
  public long sumInts() {
    long sum = 0;
    for (int value : ints) {
      sum += value;
    }
    return sum;
  }

  @Benchmark
  public long sumIntegers() {
    long sum = 0;
    for (Integer value : integers) {
      sum += value;
    }
    return sum;
  }

  @Benchmark
  public long sumYears() {
    long sum = 0;
    for (LocalDate date : dates) {
      sum += date.getYear();
    }
    return sum;
  }

  private static void shuffle(int[] array, Random random) {
    for (int i = array.length - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      int tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    }
  }

  private static <T> void shuffle(T[] array, Random random) {
    for (int i = array.length - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      T tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    }
  }
}
