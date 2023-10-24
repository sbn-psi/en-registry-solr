package gov.nasa.pds.registry.query;

import gov.nasa.pds.registry.model.ObjectStatus;

/**
 * This class identifies all the attributes one would filter on for any type of {@link RegistryObject}. This is used to build up a query from classes that extend {@link RegistryQuery}
 * @author pramirez
 *
 */
public class ObjectFilter {
	protected String guid;
	protected String name;
	protected String lid;
	protected String versionName;
	protected String objectType;
	protected ObjectStatus status;
	
	protected ObjectFilter() {
	}
	
	public static class Builder extends AbstractBuilder {
		private ObjectFilter filter;
		
		public Builder() {
			filter = new ObjectFilter();
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
    
		public ObjectFilter build() {
			this.checkBuilt();
			this.isBuilt = true;
			return this.filter;
		}
		
	}

	public String getGuid() {
		return guid;
	}

	public String getName() {
		return name;
	}

	public String getLid() {
		return lid;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getObjectType() {
		return objectType;
	}

	public ObjectStatus getStatus() {
		return status;
	}
}
