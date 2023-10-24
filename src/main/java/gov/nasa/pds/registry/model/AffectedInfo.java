package gov.nasa.pds.registry.model;

import java.util.List;

/**
 * @author pramirez
 *
 */
public class AffectedInfo {
  private List<String> _affectedIds;
  private List<String> _affectedTypes;
  
  public AffectedInfo() {
  }
  
  public AffectedInfo(List<String> affectedIds, List<String> affectedTypes) {
    _affectedIds = affectedIds;
    _affectedTypes = affectedTypes;
  }
  
  public List<String> getAffectedIds() {
    return _affectedIds;
  }
  
  public List<String> getAffectedTypes() {
    return _affectedTypes;
  }

}
