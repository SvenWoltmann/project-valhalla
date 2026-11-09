import java.math.BigInteger;

void main() {
  Number n = 1996;
  IO.println(n.getClass().getSimpleName() + ": " + Objects.hasIdentity(n));
  n = BigInteger.valueOf(1996);
  IO.println(n.getClass().getSimpleName() + ": " + Objects.hasIdentity(n));
}
