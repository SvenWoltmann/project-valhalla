import java.lang.ref.WeakReference;

void main() {
  Object date = LocalDate.of(2027, 3, 9);

  try {
    synchronized (date) {
      IO.println("locked");
    }
  } catch (IdentityException e) {
    IO.println(e);
  }

  try {
    date.notify();
  } catch (IllegalMonitorStateException e) {
    IO.println(e);
  }

  try {
    new WeakReference<>(date);
  } catch (IdentityException e) {
    IO.println(e);
  }

  try {
    new WeakHashMap<Object, String>().put(date, "Release");
  } catch (IdentityException e) {
    IO.println(e);
  }
}
