value class ValueMoney {
  private long cents;

  ValueMoney(long cents) {
    this.cents = cents;
  }

  long cents() { return cents; }
}
