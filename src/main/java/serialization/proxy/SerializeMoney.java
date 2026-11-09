import java.io.*;

void main() throws IOException, ClassNotFoundException {
  var bytes = new ByteArrayOutputStream();
  try (var out = new ObjectOutputStream(bytes)) {
    out.writeObject(new Money(1234));
  }
  try (var in = new ObjectInputStream(
      new ByteArrayInputStream(bytes.toByteArray()))) {
    Money money = (Money) in.readObject();
    IO.println(money + ", identity: " + Objects.hasIdentity(money));
  }
}
