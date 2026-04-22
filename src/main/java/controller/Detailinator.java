package controller;

public class Detailinator {
  /// makes sure top is most recent
  public static String parse(String detail, String object) {
    return detail.concat(object);
  }
}
