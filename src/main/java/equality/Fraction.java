value class Fraction {
  private int numerator;
  private int denominator;

  Fraction(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Fraction f
        && (long) numerator * f.denominator == (long) f.numerator * denominator;
  }

  @Override
  public int hashCode() {
    return Double.hashCode((double) numerator / denominator);
  }
}
