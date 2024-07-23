package gov.nasa.pds.harvest.search.inventory;

/**
 * Exception class for handling errors when reading a PDS Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryReaderException extends Exception {
    /** Generated serial ID. */
   private static final long serialVersionUID = 4687976349704354553L;

   /**
    * Holds the exception object.
    */
   private Exception exception;

   /** line number where the exception occurred. */
   private int lineNumber;

   /**
    * Constructor.
    *
    * @param exception An exception.
    */
   public InventoryReaderException(Exception exception) {
       super(exception.getMessage());
       this.exception = exception;
       lineNumber = -1;
   }

   /**
    * @return Returns the exception.
    */
   public Exception getException() {
     return exception;
   }

   /**
    * @return Returns the line number associated with the exception.
    * Could be -1 if it was not set.
    */
   public int getLineNumber() {
     return lineNumber;
   }

   /**
    * Sets the line number.
    *
    * @param line An integer value.
    */
   public void setLineNumber(int line) {
     lineNumber = line;
   }
}
