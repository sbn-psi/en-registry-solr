package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "objectRef", namespace = "http://registry.pds.nasa.gov")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectRef extends Identifiable {

	private static final long serialVersionUID = 2162651275748309952L;

	@XmlAttribute
	private String guidRef;

	@XmlAttribute
	private String homeRef;

	public ObjectRef() {
	}

	/**
	 * @return the guidRef
	 */
	public String getGuidRef() {
		return guidRef;
	}

	/**
	 * @param guidRef
	 *            the guidRef to set
	 */
	public void setGuidRef(String guidRef) {
		this.guidRef = guidRef;
	}

	/**
	 * @return the homeRef
	 */
	public String getHomeRef() {
		return homeRef;
	}

	/**
	 * @param homeRef
	 *            the homeRef to set
	 */
	public void setHomeRef(String homeRef) {
		this.homeRef = homeRef;
	}

}
