package io.github.flowrapp;

import java.util.Base64;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

  public static String basicAuth(String name, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((name + ":" + password).getBytes());
  }

}
