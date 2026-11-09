import java.io.Serializable;

value class Money implements Serializable {
  private long cents;

  Money(long cents) {
    this.cents = cents;
  }
}
