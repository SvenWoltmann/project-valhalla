import java.util.Locale;

value class UserName {
  private String name;
  private String normalized;

  UserName(String name) {
    this.name = name;
    this.normalized = normalize(name);
    super();
    IO.println("Created: " + this);
  }

  private static String normalize(String name) {
    return name.strip().toLowerCase(Locale.ROOT);
  }

  @Override
  public String toString() {
    return name + " (" + normalized + ")";
  }
}
