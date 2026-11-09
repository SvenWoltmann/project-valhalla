void main() {
  Object[] objects = {
      new Holder(), new Button(), new FieldPacking(), new BytePoints(),
      new Wrappers(), new Person("Duke", null), new Wrapper(new Event(LocalDateTime.now())),
      new Point[1], new ShortPoint[1], new BytePoint[1], new Event[1],
      new LocalDate[1], new Integer[1], new Long[1], new Object[1]
  };
  IO.println(objects.length + " objects created");
}
