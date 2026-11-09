value record Point(int x, int y) implements Cloneable {
  @Override
  public Point clone() throws CloneNotSupportedException {
    return (Point) super.clone();
  }
}

void main() throws CloneNotSupportedException {
  Point p = new Point(17, 3);
  Point copy = p.clone();
  IO.println(copy == p);
}
