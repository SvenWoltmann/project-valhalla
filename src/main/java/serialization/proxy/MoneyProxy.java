import java.io.*;

record MoneyProxy(long cents) implements Serializable {
  @Serial
  private Object readResolve() {
    return new Money(cents);
  }
}
