import java.lang.reflect.Field;

void main() throws ReflectiveOperationException {
  var identityMoney = new IdentityMoney(1234);
  Field field = IdentityMoney.class.getDeclaredField("cents");
  field.setAccessible(true);
  field.setLong(identityMoney, 9999);
  IO.println(identityMoney.cents());

  var valueMoney = new ValueMoney(1234);
  field = ValueMoney.class.getDeclaredField("cents");
  field.setAccessible(true);
  try {
    field.setLong(valueMoney, 9999);
  } catch (IllegalAccessException e) {
    IO.println(e);
  }
}
