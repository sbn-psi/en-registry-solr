package gov.nasa.pds.harvest.search.crawler.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;

/**
 * Crawler action class that checks to see if the required metadata
 * is missing.
 *
 * @author mcayanan
 *
 */
public class LogMissingReqMetadataAction extends CrawlerAction {
    private static Logger log = Logger.getLogger(
            LogMissingReqMetadataAction.class.getName());
    private List<String> reqMetadata;
    private final String ID = "LogMissingReqMetadataAction";
    private final String DESCRIPTION = "Report missing required metadata.";

    public LogMissingReqMetadataAction(List<String> reqMetadata) {
        super();
        this.reqMetadata = new ArrayList<String>();
        this.reqMetadata.addAll(reqMetadata);
        String []phases = {CrawlerActionPhases.PRE_INGEST};
        setPhases(Arrays.asList(phases));
        setId(ID);
        setDescription(DESCRIPTION);
    }

    @Override
    public boolean performAction(File product, Metadata productMetadata)
            throws CrawlerActionException {
        boolean passFlag = true;
        if (productMetadata.getHashtable().isEmpty()) {
          return false;
        }
        for (String key : reqMetadata) {
            if (!productMetadata.containsKey(key)) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                        "Missing required metadata: " + key,
                        product));
                passFlag = false;
            }
        }
        return passFlag;
    }

}
