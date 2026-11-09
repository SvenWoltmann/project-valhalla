import java.util.Locale;

value class UserName {
  private String name;
  private String normalized;

  UserName(String name) {
    this.name = name;
    this.normalized = normalize();
  }

  private String normalize() {
    return name.strip().toLowerCase(Locale.ROOT);
  }
}
