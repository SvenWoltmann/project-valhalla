import java.lang.ref.WeakReference;

void main() {
  LocalDate date = LocalDate.of(2027, 3, 9);
  WeakReference<LocalDate> ref = new WeakReference<>(date);
  IO.println(ref.get());
}
