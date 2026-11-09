void main() {
  Pixel p1 = new Pixel(17, 3);
  Pixel p2 = new Pixel(17, 3);
  Pixel p3 = new Pixel(3, 17);
  IO.println(p1.equals(p2) + " " + p1.equals(p3));
  IO.println(p1.hashCode() + " " + p2.hashCode() + " " + p3.hashCode());
  IO.println(System.identityHashCode(p1));
  IO.println(p1);
}
