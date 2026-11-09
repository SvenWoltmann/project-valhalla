record Node(String label, List<Node> edges) {
  Node {
    requireNonNull(label, this);
    requireNonNull(edges, this);
  }

  static void requireNonNull(Object arg, Object owner) {
    if (arg == null) {
      throw new IllegalArgumentException("null argument for " + owner);
    }
  }
}

void main() {
  IO.println(new Node("A", List.of()));
}
