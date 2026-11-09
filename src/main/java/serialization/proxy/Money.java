import java.io.*;

value class Money implements Serializable {
  private long cents;

  Money(long cents) {
    this.cents = cents;
  }

  @Serial
  private Object writeReplace() {
    return new MoneyProxy(cents);
  }

  @Override
  public String toString() {
    return "%d,%02d €".formatted(cents / 100, cents % 100);
  }
}
