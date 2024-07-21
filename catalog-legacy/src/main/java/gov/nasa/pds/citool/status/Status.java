package gov.nasa.pds.citool.status;

/**
 * This enum represents the status of parsing and validation against a
 * particular file. It is used in reporting and to do determine the overall exit
 * value of the Vtool command line.
 *
 * @author pramirez
 *
 */
public enum Status {
  PASS(0, "PASS"), FAIL(1, "FAIL"), SKIP(2, "SKIP"), UNKNOWN(3, "SKIP");

  private final int value;
  private final String name;

  private Status(final int value, final String name) {
      this.value = value;
      this.name = name;
  }

  public int getValue() {
    return this.value;
  }

  public String getName() {
    return this.name;
  }
}

