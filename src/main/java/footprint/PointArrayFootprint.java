import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

static final int COUNT = 1_000_000;

void main() throws Exception {
  var valuePoints = new ValuePoint[COUNT];
  var shortPoints = new ShortPoint[COUNT];
  var identityPoints = new IdentityPoint[COUNT];
  for (int i = 0; i < COUNT; i++) {
    valuePoints[i] = new ValuePoint(i, -i);
    shortPoints[i] = new ShortPoint((short) i, (short) -i);
    identityPoints[i] = new IdentityPoint(i, -i);
  }
  printHistogram();
  IO.println(valuePoints.length + shortPoints.length + identityPoints.length);
}

void printHistogram() throws Exception {
  var server = ManagementFactory.getPlatformMBeanServer();
  var command = new ObjectName("com.sun.management:type=DiagnosticCommand");
  String histogram = (String) server.invoke(command, "gcClassHistogram",
      new Object[] { null }, new String[] { String[].class.getName() });
  histogram.lines()
      .filter(line -> line.contains("num ") || line.contains("ValuePoint")
          || line.contains("ShortPoint") || line.contains("IdentityPoint"))
      .forEach(IO::println);
}
