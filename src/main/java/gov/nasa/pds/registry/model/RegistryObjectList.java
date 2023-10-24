package gov.nasa.pds.registry.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author pramirez
 *
 */
@XmlRootElement(name = "registryObjectList", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryObjectList {
  @XmlElementRef
  private List<? extends RegistryObject> objects;
  
  public RegistryObjectList() {
    objects = null;
  }
  
  public List<? extends RegistryObject> getObjects() {
    return this.objects;
  }

  public void setObjects(List<? extends RegistryObject> objects) {
    this.objects = new ArrayList<RegistryObject>(objects);
  }
}
