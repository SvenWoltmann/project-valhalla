value class Square extends Shape {
  private double side;

  Square(double side) {
    this.side = side;
  }

  @Override
  double area() { return side * side; }
}
