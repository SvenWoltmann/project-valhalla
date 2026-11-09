value record Node(int value, Node next) {}

void main() {
  Node a = null;
  Node b = null;
  for (int i = 0; i < 100_000; i++) {
    a = new Node(i, a);
    b = new Node(i, b);
  }
  IO.println(a == b);
}
