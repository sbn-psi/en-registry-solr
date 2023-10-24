package gov.nasa.pds.registry.query;

/**
 * @author pramirez
 *
 */
public class AbstractBuilder {
	protected boolean isBuilt;
	
	public void checkBuilt() {
		if (this.isBuilt) {
			throw new IllegalStateException("The object cannot be modified after built.");
		}
	}

}
