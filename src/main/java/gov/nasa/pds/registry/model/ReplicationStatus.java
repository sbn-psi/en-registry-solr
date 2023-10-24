package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author pramirez
 *
 */@XmlType(name = "")
 @XmlEnum
public enum ReplicationStatus {
   RUNNING, COMPLETE;
}
