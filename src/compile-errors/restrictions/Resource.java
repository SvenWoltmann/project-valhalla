value class Resource {
  private int handle;

  Resource(int handle) {
    this.handle = handle;
  }

  @Override
  protected void finalize() {
    IO.println("finalize " + handle);
  }
}
