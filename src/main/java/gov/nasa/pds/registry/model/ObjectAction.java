package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum cpatures the mapping from actions take to events created.
 * 
 * @author pramirez
 * 
 */
@XmlType(name = "")
@XmlEnum
public enum ObjectAction {
  approve(ObjectStatus.Approved, EventType.Approved), deprecate(
      ObjectStatus.Deprecated, EventType.Deprecated), undeprecate(
      ObjectStatus.Submitted, EventType.Undeprecated);

  private final ObjectStatus status;
  private final EventType type;

  private ObjectAction(ObjectStatus status, EventType type) {
    this.status = status;
    this.type = type;
  }

  public ObjectStatus getObjectStatus() {
    return this.status;
  }

  public EventType getEventType() {
    return this.type;
  }
}
