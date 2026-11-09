import java.lang.reflect.Field;
import sun.misc.Unsafe;

value record ValuePoint(int x, int y) {}
record IdentityPoint(int x, int y) {}

void main() throws ReflectiveOperationException {
  Field f = Unsafe.class.getDeclaredField("theUnsafe");
  f.setAccessible(true);
  Unsafe unsafe = (Unsafe) f.get(null);
  for (Object o : new Object[] { new IdentityPoint(17, 3), new ValuePoint(17, 3),
                                 new LocalDate[2], new Integer[2], new Object[2] }) {
    long mark = unsafe.getLong(o, 0L);
    IO.println("%-22s %64s".formatted(o.getClass().getSimpleName(),
        Long.toBinaryString(mark)).replace(' ', ' '));
  }
}
