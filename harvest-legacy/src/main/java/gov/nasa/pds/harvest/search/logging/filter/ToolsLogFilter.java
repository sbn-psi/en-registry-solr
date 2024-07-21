package gov.nasa.pds.harvest.search.logging.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Class to filter logging messages that are coming from the underlying
 * framework.
 *
 * @author mcayanan
 *
 */
public class ToolsLogFilter implements Filter {

    /**
     * Method that checks if a log record is loggable.
     *
     * @param record The LogRecord.
     *
     * @return true if the record can be logged by the handler.
     *
     */
    @Override
    public boolean isLoggable(LogRecord record) {
        String casCrawlerName = "gov.nasa.jpl.oodt.cas.crawl";
        String jerseyName = "com.sun.jersey.core";
        String fileMgrVersionerName = "org.apache.oodt.cas.filemgr.versioning";
        String inPlacedataTransferName =
          "org.apache.oodt.cas.filemgr.datatransfer.InPlaceDataTransferer";
        if ((record.getLoggerName() != null)
                && (record.getLoggerName().contains(casCrawlerName)
                    || record.getLoggerName().contains(jerseyName)
                    || record.getLoggerName().contains(fileMgrVersionerName)
                    || record.getLoggerName().contains(inPlacedataTransferName)
                    )) {
            return false;
        } else {
            return true;
        }
    }
}
