import java.io.*;

void main() throws IOException, ClassNotFoundException {
  IO.println(roundTrip(new Point(17, 3)));
  try {
    roundTrip(new Money(1234));
  } catch (InvalidClassException e) {
    IO.println(e);
  }
}

Object roundTrip(Object o) throws IOException, ClassNotFoundException {
  var bytes = new ByteArrayOutputStream();
  try (var out = new ObjectOutputStream(bytes)) {
    out.writeObject(o);
  }
  try (var in = new ObjectInputStream(
      new ByteArrayInputStream(bytes.toByteArray()))) {
    return in.readObject();
  }
}
