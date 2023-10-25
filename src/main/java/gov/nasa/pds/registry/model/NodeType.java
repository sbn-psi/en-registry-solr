package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the type of codes in a particular classification scheme.
 * 
 * @author pramirez
 * 
 */
@XmlType(name = "")
@XmlEnum
public enum NodeType {
  UniqueCode, EmbeddedPath, NonUniqueCode;
}
