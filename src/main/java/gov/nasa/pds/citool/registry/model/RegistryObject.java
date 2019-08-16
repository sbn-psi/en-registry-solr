//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.citool.registry.model;


public class RegistryObject 
{
	private String guid;
	private String lid;
  
	// Alternative LID (Instrument host can have one, e.g., 
	// main = urn:nasa:pds:context_pds3:instrument_host:spacecraft.go
	// alternative = urn:nasa:pds:context_pds3:instrument_host:instrument_host.gp)
	private String altLid;
  
	private String name;
	private String objectType;

	private String description;
	private String versionName;
	private String md5Hash;

	private Slots slots;

	public RegistryObject() 
	{
	}
  
  	public String getGuid() {
	    return guid;
  	}
  
  	public String getLid() 
  	{
  		return lid;
  	}

  	public String getAltLid() 
  	{
  		return altLid;
  	}
  
  	public void setGuid(String guid) {
	    this.guid = guid;
  	}

	public void setLid(String lid) 
	{
		this.lid = lid;
	}

	public void setAltLid(String lid) 
	{
		this.altLid = lid;
	}

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the objectType
   */
  public String getObjectType() {
    return objectType;
  }

  /**
   * @param objectType
   *          the objectType to set
   */
  public void setObjectType(String objectType) 
  {
	  this.objectType = objectType;
  }
  
  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the internally tracked version
   */
  public String getVersionName() {
    return versionName;
  }

  /**
   * @param versionName
   *          the version to set for the registry tracked version
   */
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  
	public String getMd5Hash() 
	{
		return md5Hash;
	}

	public void setMd5Hash(String md5Hash) 
	{
		this.md5Hash = md5Hash;
	}

  
	public Slots getSlots()
	{
		return slots;
	}
	
	public void setSlots(Slots slots)
	{
		this.slots = slots;
	}
	

  @Override
  public int hashCode() 
  {
    final int prime = 31;
    int result = super.hashCode();
    
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((lid == null) ? 0 : lid.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
    result = prime * result + ((versionName == null) ? 0 : versionName.hashCode());

    return result;
  }


@Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    RegistryObject other = (RegistryObject) obj;
    
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    
    if (lid == null) {
      if (other.lid != null)
        return false;
    } else if (!lid.equals(other.lid))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (objectType == null) {
      if (other.objectType != null)
        return false;
    } else if (!objectType.equals(other.objectType))
      return false;
    
    if (versionName == null) {
      if (other.versionName != null)
        return false;
    } else if (!versionName.equals(other.versionName))
      return false;
    return true;
  }

}
