package gov.nasa.pds.harvest.search.crawler.actions;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.oodt.crawler.CrawlerAction;
import gov.nasa.pds.harvest.search.oodt.filemgr.CrawlerActionPhases;
import gov.nasa.pds.harvest.search.oodt.filemgr.exceptions.CrawlerActionException;
import gov.nasa.pds.harvest.search.oodt.metadata.Metadata;

/**
 * Pre-ingest Crawler Action that checks to see that the title value is
 * less than 255 characters.
 *
 * @author mcayanan
 *
 */
public class TitleLengthCheckerAction extends CrawlerAction {

  /** Logger object. */
  private static Logger log = Logger.getLogger(
      TitleLengthCheckerAction.class.getName());

  /** Crawler action id. */
  private final static String ID = "TitleLengthCheckerAction";

  /** Crawler action description. */
  private final static String DESCRIPTION = "Checks to see that the title "
      + "value does not exceed 255 characters.";

  /**
   * Constructor.
   */
  public TitleLengthCheckerAction() {
    super();
    String[] phases = {CrawlerActionPhases.PRE_INGEST.getName()};
    setPhases(Arrays.asList(phases));
    setId(ID);
    setDescription(DESCRIPTION);
  }

  /**
   * Performs the crawler action that verifies that the title value
   * is less than 255 characters.
   *
   * @param product The product file.
   * @param metadata The product metadata.
   *
   * @throws CrawlerActionException None thrown.
   *
   */
  public boolean performAction(File product, Metadata metadata)
      throws CrawlerActionException {
    boolean passFlag = true;
    if (metadata.containsKey(Constants.TITLE)) {
      if (metadata.getMetadata(Constants.TITLE).length()
          > Constants.TITLE_MAX_LENGTH) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
            "Title metadata value exceeds " + Constants.TITLE_MAX_LENGTH
            + " characters: "
                + metadata.getMetadata(Constants.TITLE), product));
        passFlag = false;
      }
    }
    return passFlag;
  }


}
