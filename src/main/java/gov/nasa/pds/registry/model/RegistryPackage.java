package gov.nasa.pds.registry.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * RegistryPackage instances allow for grouping of logically related
 * RegistryObject instances even if individual member objects belong to
 * different Submitting Organizations.
 * 
 * @author pramirez
 * 
 */
@Entity
@XmlRootElement(name = "registryPackage", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryPackage extends RegistryObject {

  private static final long serialVersionUID = -1473775847650130871L;

  public RegistryPackage() {
    this.setObjectType(RegistryPackage.class.getSimpleName());
  }

}
