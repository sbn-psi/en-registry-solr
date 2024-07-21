package gov.nasa.pds.harvest.search.util;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;

/**
 * Class that recursively finds pointer statements in a PDS3 label.
 *
 * @author mcayanan
 *
 */
public class PointerStatementFinder {
  /**
   * Finds all pointer statements in a given label.
   *
   * @param label A PDS3 label.
   *
   * @return A list of pointer statements found within the given label.
   */
  static public List<PointerStatement> find(Label label) {
    List<PointerStatement> result = new ArrayList<PointerStatement>();
    result.addAll(label.getPointers());
    for (ObjectStatement object : label.getObjects()) {
      result.addAll(find(object));
    }
    return result;
  }

  /**
   * Finds all pointer statements in a given object statement.
   *
   * @param object An object statement.
   *
   * @return A list of pointer statements found within a given label.
   */
  static private List<PointerStatement> find(ObjectStatement object) {
    List<PointerStatement> result = new ArrayList<PointerStatement>();
    result.addAll(object.getPointers());
    for (ObjectStatement o : object.getObjects()) {
      result.addAll(find(o));
    }
    return result;
  }
}
