package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum captures the core event types for {@link AuditableEvent}
 * 
 * @author pramirez
 * 
 */
@XmlType(name = "")
@XmlEnum
public enum EventType {
	Created, Approved, Deleted, Updated, Deprecated, Versioned, Undeprecated, Replicated;
}
