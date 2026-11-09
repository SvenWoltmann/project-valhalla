void main() {
  LocalDate date = LocalDate.of(2027, 3, 9);
  synchronized (date) {
    IO.println("locked");
  }
}
