void main() {
  Integer i = 1996, j = 1996;
  IO.println(i == j);

  LocalDate d1 = LocalDate.of(2027, 3, 9);
  LocalDate d2 = d1.plusYears(1).minusYears(1);
  IO.println(d1 == d2);
  IO.println(d1.equals(d2));
}
