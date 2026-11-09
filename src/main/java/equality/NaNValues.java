value record Measurement(double value) {}

void main() {
  double nan1 = Double.longBitsToDouble(0x7ff8000000000000L);
  double nan2 = Double.longBitsToDouble(0x7ff8000000000001L);
  Measurement m1 = new Measurement(nan1);
  Measurement m2 = new Measurement(nan2);
  IO.println(m1 + " / " + m2);
  IO.println(m1.equals(m2));
  IO.println(m1 == m2);
}
