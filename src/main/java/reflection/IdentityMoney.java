class IdentityMoney {
  private final long cents;

  IdentityMoney(long cents) {
    this.cents = cents;
  }

  long cents() { return cents; }
}
