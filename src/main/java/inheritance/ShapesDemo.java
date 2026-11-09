void main() {
  Shape shape = new Square(3);
  IO.println(shape.area());
  IO.println(Objects.hasIdentity(shape));
}
