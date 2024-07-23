package gov.nasa.pds.registry.model;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Simple class to capture references to be used in HTTP headers or in responses
 * 
 * @author pramirez
 * 
 */
@Embeddable
@XmlRootElement(name = "link", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {
  @XmlAttribute
  private String href;

  @XmlAttribute(name = "rel")
  private String relationship;

  @XmlAttribute
  private String type;

  // Needed for JAXB
  public Link() {
    this.href = null;
    this.type = null;
  }

  public Link(String href, String relationship, String type) {
    this.href = href;
    this.relationship = relationship;
    this.type = type;
  }

  public String getHref() {
    return href;
  }

  public String getRelationship() {
    return relationship;
  }

  public String getType() {
    return type;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder("<");
    builder.append(href).append(">; rel=").append(relationship);
    if (type != null)
      builder.append("; type=").append(type);
    return builder.toString();
  }
}
