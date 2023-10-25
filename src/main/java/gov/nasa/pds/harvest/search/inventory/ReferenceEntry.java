package gov.nasa.pds.harvest.search.inventory;

import java.io.File;

/**
 * Class that holds metadata of an association.
 *
 * @author mcayanan
 *
 */
public class ReferenceEntry {
    /** Logical identifier. */
    private String logicalID;

    /** Version. */
    private String version;

    /** The guid. */
    private String guid;

    /** The reference type. */
    private String type;

    /** Flag to indicate whether the association has a LID-VID reference. */
    private boolean hasVersion;

    /** The file associated with this entry */
    private File file;

    /** The location of this association in the file */
    private int lineNumber;

    /**
     * Constructor.
     *
     */
    public ReferenceEntry() {
        logicalID = null;
        version = null;
        type = null;
        lineNumber = -1;
        file = null;
        guid = null;

        hasVersion = false;
    }

    /**
     * Get the logical identifier.
     *
     * @return A LID.
     */
    public String getLogicalID() {
        return logicalID;
    }

    /**
     * Set the logical identifier.
     *
     * @param id A LID.
     */
    public void setLogicalID(String id) {
        logicalID = id;
    }

    /**
     * Get the version.
     *
     * @return A version ID.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param ver A version ID.
     */
    public void setVersion(String ver) {
        version = ver;
        hasVersion = true;
    }

    /**
     * Flag to indicate if the association contains
     * a version.
     *
     * @return 'true' if the association has a LID-VID
     * reference.
     */
    public boolean hasVersion() {
        return hasVersion;
    }

    /**
     * Get the reference type.
     *
     * @return A type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the reference type.
     *
     * @param type A type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the file associated with the reference entry.
     *
     * @param file The file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the file.
     *
     * @return The file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the line number associated with the reference entry.
     *
     * @param num A line number.
     */
    public void setLineNumber(int num) {
        lineNumber = num;
    }

    /**
     * Gets the line number.
     *
     * @return The line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    public String getGuid() {
      return guid;
    }

    /**
     * Set the guid.
     *
     * @param guid A guid.
     */
    public void setGuid(String guid) {
      this.guid = guid;
    }

    /**
     * Determines if the guid has been set.
     *
     * @return true if the guid is not null.
     */
    public boolean hasGuid() {
      if (guid != null) {
        return true;
      } else {
        return false;
      }
    }
}
