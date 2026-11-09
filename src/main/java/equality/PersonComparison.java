value record Person(String name, LocalDate birthday) {}

void main() {
  Person p1 = new Person("Duke", LocalDate.of(1995, 5, 23));
  Person p2 = new Person("Duke", LocalDate.of(1995, 5, 23));
  Person p3 = new Person(new String("Duke"), LocalDate.of(1995, 5, 23));
  IO.println(p1 == p2);
  IO.println(p1 == p3);
  IO.println(p1.equals(p3));
}
