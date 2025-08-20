package io.github.flowrapp.value;

public record SensitiveInfo<T>(
    T value) {

  public static <T> SensitiveInfo<T> of(T t) {
    return new SensitiveInfo<>(t);
  }

  public static <T> SensitiveInfo<T> empty() {
    return new SensitiveInfo<>(null);
  }

  public T get() {
    return value;
  }

  @Override
  public String toString() {
    return value != null ? "****" : null;
  }

}
