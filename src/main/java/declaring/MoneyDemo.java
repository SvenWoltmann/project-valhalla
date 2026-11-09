void main() {
  Money a = new Money(12, 34);
  Money b = new Money(12, 34);
  IO.println(a + " == " + b + ": " + (a == b));
  IO.println(Objects.hasIdentity(a));
}
