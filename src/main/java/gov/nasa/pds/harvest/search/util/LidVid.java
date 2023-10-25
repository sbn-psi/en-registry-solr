package gov.nasa.pds.harvest.search.util;

/**
 * Class that represents the lidvid of a PDS4 data product.
 *
 * @author mcayanan
 *
 */
public class LidVid {

  /** The logical identifier. */
  private String lid;

  /** The version. */
  private String version;

  /** Flag to indicate if a version exists. */
  private boolean hasVersion;

  public LidVid(String lid) {
    this(lid, null);
  }

  public LidVid(String lid, String version) {
    this.lid = lid;
    this.version = version;
    if (this.version == null) {
      hasVersion = false;
    } else {
      hasVersion = true;
    }
  }

  public String getLid() {
    return this.lid;
  }

  public String getVersion() {
    return this.version;
  }

  public boolean hasVersion() {
    return this.hasVersion;
  }

  public String toString() {
    String identifier = this.lid;
    if (hasVersion) {
      identifier += "::" + this.version;
    }
    return identifier;
  }

  /**
   * Determines where 2 LIDVIDs are equal.
   *
   */
  public boolean equals(Object o) {
    boolean isEqual = false;
    LidVid lidvid = (LidVid) o;
    if (this.lid.equals(lidvid.getLid())) {
      if (this.hasVersion) {
        if (lidvid.hasVersion()
            && this.version.equals(lidvid.getVersion())) {
          isEqual = true;
        }
      } else {
        isEqual = true;
      }
    }
    return isEqual;
  }
}
