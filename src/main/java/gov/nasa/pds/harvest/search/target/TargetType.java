package gov.nasa.pds.harvest.search.target;

/**
 * An enum of the different types of targets that can be passed into the
 * Validate Tool.
 *
 * @author mcayanan
 *
 */
public enum TargetType {
    BUNDLE(0, "bundle"), COLLECTION(1, "collection"),
    DIRECTORY(2, "directory"), FILE(3, "file");

    private final int value;
    private final String name;

    private TargetType(final int value, final String name) {
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
