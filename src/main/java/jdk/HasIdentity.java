void main() {
  IO.println(Objects.hasIdentity(1996));
  IO.println(Objects.hasIdentity(LocalDate.of(2027, 3, 9)));
  IO.println(Objects.hasIdentity(Optional.of("Duke")));
  IO.println(Objects.hasIdentity("Duke"));
  IO.println(Objects.hasIdentity(new ArrayList<String>()));
}
