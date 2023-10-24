package gov.nasa.pds.harvest.search.crawler.metadata.extractor;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.pds.harvest.search.policy.Pds4ProductMetadata;
import gov.nasa.pds.harvest.search.policy.ReferenceTypeMap;
import gov.nasa.pds.harvest.search.policy.References;
import gov.nasa.pds.harvest.search.policy.XPath;

/**
 * Configuration class for extracting metadata from
 * PDS4 data products.
 *
 * @author mcayanan
 *
 */
public class Pds4MetExtractorConfig implements MetExtractorConfig {
    /** Candidate products. */
    private List<Pds4ProductMetadata> pds4Candidates;

    private References references;
    /**
     * Default contstructor.
     *
     * @param candidates A class that contains what product types
     * to extract and which metadata fields to get from those
     * product types.
     *
     */
    public Pds4MetExtractorConfig(List<Pds4ProductMetadata> candidates, References references) {
      pds4Candidates = candidates;
      this.references = references;
    }

    /**
     * Gets XPath expressions for an object type.
     *
     * @param objectType The PDS object type.
     *
     * @return A list of XPath expressions based on the given object type.
     */
    public List<XPath> getMetXPaths(String objectType) {
        for (Pds4ProductMetadata p : pds4Candidates) {
            if (p.getObjectType().equalsIgnoreCase(objectType)) {
                return p.getXPath();
            }
        }
        return new ArrayList<XPath>();
    }

    /**
     * Determines whether an object type exists in the configuration class.
     *
     * @param objectType The object type to search.
     *
     * @return true if the supplied object type was found.
     */
    public boolean hasObjectType(String objectType) {
        for (Pds4ProductMetadata p : pds4Candidates) {
            if (p.getObjectType().equalsIgnoreCase(objectType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the mapped reference type of the given model value.
     *
     * @param modelValue The model value.
     *
     * @return The mapped reference type associated with the given model
     *  value. Returns 'null' if nothing was found.
     */
    public String getReferenceTypeMap(String modelValue) {
      for (ReferenceTypeMap refMap : references.getReferenceTypeMap()) {
        for (String value : refMap.getModelValue()) {
          if (value.trim().equals(modelValue)) {
            return refMap.getValue();
          }
        }
      }
      return null;
    }

    /**
     * Determines whether the config contains a reference type map.
     *
     * @return 'true' if yes, 'false' otherwise.
     */
    public boolean containsReferenceTypeMap() {
      if (!references.getReferenceTypeMap().isEmpty()) {
        return true;
      } else {
        return false;
      }
    }
}
