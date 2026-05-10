package controller;

/**
 * Utility for formatting detail log text before it is displayed in the UI.
 */
public class Detailinator {
  /**
   * Prepends the newest detail entry to the existing detail log text.
   *
   * @param detail newest detail text
   * @param object existing detail log text
   * @return combined text with the newest detail first
   */
  public static String parse(String detail, String object) {
    return detail.concat(object);
  }
}
