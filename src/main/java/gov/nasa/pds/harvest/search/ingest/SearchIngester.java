package gov.nasa.pds.harvest.search.ingest;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.oodt.filemgr.Ingester;
import gov.nasa.pds.harvest.search.oodt.filemgr.exceptions.CatalogException;
import gov.nasa.pds.harvest.search.oodt.filemgr.exceptions.IngestException;
import gov.nasa.pds.harvest.search.oodt.metadata.MetExtractor;
import gov.nasa.pds.harvest.search.oodt.metadata.Metadata;
import gov.nasa.pds.harvest.search.registry.MetadataExtractor;
import gov.nasa.pds.harvest.search.registry.RegistryDAO;
import gov.nasa.pds.harvest.search.registry.RegistryMetadata;
import gov.nasa.pds.harvest.search.stats.HarvestSolrStats;


/**
 * Class that supports ingestion of PDS4 product labels as a blob into the PDS
 * Search Service.
 *
 * @author mcayanan
 *
 */
public class SearchIngester implements Ingester
{
	private static Logger log = Logger.getLogger(SearchIngester.class.getName());

	private MetadataExtractor metaExtractor;
	private RegistryDAO registryDAO;

	/**
	 * Default constructor.
	 */
	public SearchIngester() throws Exception
	{
	    metaExtractor = new MetadataExtractor();
	    registryDAO = new RegistryDAO();
	}


	/**
	 * Method not used at this time.
	 *
	 */
	public boolean hasProduct(URL registry, File productFile) throws CatalogException {
		// No use for this method for now
		return false;
	}

	/**
	 * Determines whether a product is already in the registry.
	 *
	 * @param registry The URL to the registry service.
	 * @param lid      The PDS4 logical identifier.
	 *
	 * @return 'true' if the logical identifier was found in the registry. 'false'
	 *         otherwise.
	 *
	 * @throws CatalogException exception ignored.
	 */
	public boolean hasProduct(URL registry, String lid) throws CatalogException {
		return false;
	}

	/**
	 * Determines whether a version of a product is already in the registry.
	 *
	 * @param registry The URL to the registry service.
	 * @param lid      The PDS4 logical identifier.
	 * @param vid      The version of the product.
	 *
	 * @return 'true' if the logical identifier and version was found in the
	 *         registry.
	 *
	 * @throws CatalogException If an error occurred while talking to the ingester.
	 */
	public boolean hasProduct(URL registry, String lid, String vid) throws CatalogException 
	{
	    String lidvid = lid + "::" + vid;
	    
	    try
	    {
    		return registryDAO.hasProduct(lid, vid);
	    }
	    catch(Exception ex)
	    {
            throw new CatalogException("Error while trying to find blob " + lidvid + ": " + ex.getMessage());
	    }
	}

	
	/**
	 * Ingests the product into the registry.
	 *
	 * @param searchUrl The URL to the Search Service.
	 * @param prodFile  The PDS4 product file.
	 * @param met       The metadata to register.
	 *
	 * @return The URL of the registered product.
	 * @throws IngestException If an error occurred while ingesting the product.
	 */
	public String ingest(URL searchUrl, File prodFile, Metadata met) throws IngestException 
	{
		String lid = met.getMetadata(Constants.LOGICAL_ID);
		String vid = met.getMetadata(Constants.PRODUCT_VERSION);
		String lidvid = lid + "::" + vid;

        for (String key : met.getAllKeys()) {
          log.info("ingest key: " + key);
        }
				
		try 
		{
          RegistryMetadata registryMeta = metaExtractor.extract(prodFile.getAbsolutePath());
			    
          // Save product file
          registryDAO.saveProduct(registryMeta, prodFile);

          log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
              "Successfully registered product: " + lidvid, prodFile));
          ++HarvestSolrStats.numProductsRegistered;

          return lidvid;
		} 
		catch(CatalogException c)
		{
			++HarvestSolrStats.numProductsNotRegistered;
			log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Error while " 
			        + "checking for the existence of a registered product: " + c.getMessage(), prodFile));
			throw new IngestException(c.getMessage());
		} 
		catch(Exception ex)
		{
		    ++HarvestSolrStats.numProductsNotRegistered;
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE, ex.getMessage(), prodFile));
		    throw new IngestException(ex);
		} 
	}

	
	
	/**
	 * Method not implemented at this time.
	 *
	 */
	public String ingest(URL fmUrl, File prodFile, MetExtractor extractor, File metConfFile) throws IngestException {
		// No need for this method at this time
		return null;
	}

	/**
	 * Method not implemented at this time.
	 *
	 */
    public void ingest(URL fmUrl, List<String> prodFiles, MetExtractor extractor,
        File metConfFile) {
		// No need for this method at this time
	}
}
