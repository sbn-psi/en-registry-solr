package gov.nasa.pds.harvest.search.inventory;

/**
 * Interface for reading a PDS Inventory File.
 *
 * @author mcayanan
 *
 */
public interface InventoryReader {

    /**
     * Get the next file reference in the Inventory file.
     *
     * @return An object representation of the next file reference in
     * the Inventory file.
     *
     * @throws InventoryReaderException
     */
    public abstract InventoryEntry getNext() throws InventoryReaderException;
}
