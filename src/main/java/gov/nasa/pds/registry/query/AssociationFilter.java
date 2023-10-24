package gov.nasa.pds.registry.query;

public class AssociationFilter extends ObjectFilter {
	private String sourceObject;
	private String targetObject;
	private String associationType;
	
	private AssociationFilter() {
		super();
	}
	
	public static class Builder extends AbstractBuilder {
		private AssociationFilter filter;
		
		public Builder() {
			filter = new AssociationFilter();
		}
		
		public Builder sourceObject(String sourceObject) {
			this.checkBuilt();
			this.filter.sourceObject = sourceObject;
			return this;
		}
		
		public Builder targetObject(String targetObject) {
			this.checkBuilt(); 
			this.filter.targetObject = targetObject;
			return this;
		}
		
		public Builder associationType(String associationType) {
			this.checkBuilt();
			this.filter.associationType = associationType;
			return this;
		}
		
		public AssociationFilter build() {
			this.checkBuilt();
			this.isBuilt = true;
			return this.filter;
		}
	}

	public String getSourceObject() {
		return sourceObject;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public String getAssociationType() {
		return associationType;
	}
}
