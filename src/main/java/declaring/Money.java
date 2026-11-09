value class Money {
  private long cents;

  Money(long euros, int cents) {
    this.cents = euros * 100 + cents;
  }

  long euros() { return cents / 100; }
  int cents() { return (int) (cents % 100); }

  @Override
  public String toString() {
    return "%d,%02d €".formatted(euros(), cents());
  }
}
