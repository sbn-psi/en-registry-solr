package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlEnum
public enum RegistryStatus {
	OK, FAIL;
}
