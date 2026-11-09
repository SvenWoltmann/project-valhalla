import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

static final int COUNT = 1_000_000;

void main() throws Exception {
  LocalDate[] dates = new LocalDate[COUNT];
  LocalDate start = LocalDate.of(2000, 1, 1);
  for (int i = 0; i < COUNT; i++) {
    dates[i] = start.plusDays(i);
  }
  printHistogram();
  IO.println(dates[COUNT - 1]);
}

void printHistogram() throws Exception {
  var server = ManagementFactory.getPlatformMBeanServer();
  var command = new ObjectName("com.sun.management:type=DiagnosticCommand");
  String histogram = (String) server.invoke(command, "gcClassHistogram",
      new Object[] { null }, new String[] { String[].class.getName() });
  histogram.lines()
      .filter(line -> line.contains("num ") || line.contains("java.time.LocalDate ")
          || line.contains("[Ljava.time.LocalDate;"))
      .forEach(IO::println);
}
