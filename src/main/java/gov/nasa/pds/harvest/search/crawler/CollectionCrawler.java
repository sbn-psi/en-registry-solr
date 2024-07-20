package gov.nasa.pds.harvest.search.crawler;

import java.io.File;
import java.util.logging.Logger;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.crawler.metadata.extractor.Pds4MetExtractorConfig;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.oodt.crawler.CrawlerActionRepo;

/**
 * A crawler class for a PDS Collection file.
 *
 * @author mcayanan
 *
 */
public class CollectionCrawler extends PDSProductCrawler {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
      CollectionCrawler.class.getName());

  /**
   * Constructor.
   *
   * @param extractorConfig A configuration class for the metadata
   * extractor.
   */
  public CollectionCrawler(Pds4MetExtractorConfig extractorConfig) {
    super(extractorConfig);
  }

  /**
   * Crawl a PDS4 collection file. Method will register the collection
   * first before attempting to register the product files it is pointing
   * to.
   *
   * @param collection The PDS4 Collection file.
   *
   */
  public void crawl(File collection) {
    //Load actions first before crawling
    CrawlerActionRepo repo = new CrawlerActionRepo();
    repo.loadActions(getActions());
    setActionRepo(repo);
    if (collection.canRead()) {
      handleFile(collection);
      Constants.collections.add(collection);
    } else {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Unreadable target: "
          + collection));
    }
  }
}
