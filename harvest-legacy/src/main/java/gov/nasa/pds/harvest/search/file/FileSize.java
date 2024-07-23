package gov.nasa.pds.harvest.search.file;

/**
 * Class representation of being able to specify units along with a file size.
 * 
 * @author mcayanan
 *
 */
public class FileSize {
  private long size;
  private String units;
  
  /**
   * Constructor.
   * 
   * @param size The file size.
   * @param units A units designation.
   */
  public FileSize(long size, String units) {
    this.size = size;
    this.units = units;
  }
  
  /**
   * @return the size.
   */
  public long getSize() {
    return this.size;
  }
  
  /**
   * @return the units.
   */
  public String getUnits() {
    return this.units;
  }
  
  /**
   * @return Determines whether a units value was defined.
   */
  public boolean hasUnits() {
    if (!this.units.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
}
