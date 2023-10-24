package gov.nasa.pds.harvest.search.inventory;

import java.io.File;

/**
 * Class representation of a single entry in a PDS Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryEntry {
    /** A product file. */
    private File file;

    /** A checksum. */
    private String checksum;

    /** A logical identifier. */
    private String identifier;

    /** Member status. */
    private String memberStatus;

    /** Default constructor */
    public InventoryEntry() {
        this.file = null;
        this.checksum = "";
        this.identifier = "";
        this.memberStatus = "";
    }

    /**
     * Constructor.
     *
     * @param identifier logical identifier.
     * @param memberStatus member status.
     */
    public InventoryEntry(String identifier, String memberStatus) {
      this(null, "", identifier, memberStatus);
    }

    /**
     * Constructor.
     *
     * @param file A product file.
     * @param checksum checksum.
     * @param identifier logical identifier.
     */
    public InventoryEntry(File file, String checksum, String identifier, String memberStatus) {
        this.file = file;
        this.checksum = checksum;
        this.identifier = identifier;
        this.memberStatus = memberStatus;
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
     * Gets the checksum.
     *
     * @return Checksum value.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Gets the logical identifier.
     *
     * @return A LID or LIDVID.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the member status.
     *
     * @return "P", "Primary", "S", or "Secondary"
     */
    public String getMemberStatus() {
      return memberStatus;
    }

    /**
     * Determines whether the object is empty.
     *
     * @return true if the object is empty, false otherwise.
     */
    public boolean isEmpty() {
        if (this.file == null && this.checksum.isEmpty()
            && this.identifier.isEmpty() && this.memberStatus.isEmpty()) {
          return true;
        } else {
          return false;
        }
    }
}
