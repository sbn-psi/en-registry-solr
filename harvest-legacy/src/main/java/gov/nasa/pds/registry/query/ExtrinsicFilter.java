package gov.nasa.pds.registry.query;

import gov.nasa.pds.registry.model.ObjectStatus;

/**
 * This class supports filtering on an extrinsics attributes.
 * 
 * @author pramirez
 * 
 */
public class ExtrinsicFilter extends ObjectFilter {
  private String contentVersion;
  private String mimeType;

  private ExtrinsicFilter() {
    super();
  }

  public static class Builder extends AbstractBuilder {
    private ExtrinsicFilter filter;

    public Builder() {
      filter = new ExtrinsicFilter();
    }

    public Builder contentVersion(String contentVersion) {
      this.checkBuilt();
      this.filter.contentVersion = contentVersion;
      return this;
    }

    public Builder mimeType(String mimeType) {
      this.checkBuilt();
      this.filter.mimeType = mimeType;
      return this;
    }
    
    public Builder guid(String guid) {
      this.checkBuilt();
      this.filter.guid = guid;
      return this;
    }
    
    public Builder name(String name) {
      this.checkBuilt();
      this.filter.name = name;
      return this;
    }
    
    public Builder lid(String lid) {
      this.checkBuilt();
      this.filter.lid = lid;
      return this;
    }
    
    public Builder versionName(String versionName) {
      this.checkBuilt();
      this.filter.versionName = versionName;
      return this;
    }
    
    public Builder objectType(String objectType) {
      this.checkBuilt();
      this.filter.objectType = objectType;
      return this;
    }
    
    public Builder status(ObjectStatus status) {
      this.checkBuilt();
      this.filter.status = status;
      return this;
    }

    public ExtrinsicFilter build() {
      this.checkBuilt();
      this.isBuilt = true;
      return this.filter;
    }
  }
  
  public String getContentVersion() {
    return contentVersion;
  }

  public String getMimeType() {
    return mimeType;
  }

}
