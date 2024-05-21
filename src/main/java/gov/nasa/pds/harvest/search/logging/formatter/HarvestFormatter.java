package gov.nasa.pds.harvest.search.logging.formatter;

import java.math.BigInteger;
import java.util.Map.Entry;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.stats.HarvestSolrStats;

/**
 * Class that formats the Harvest logging messages.
 *
 * @author mcayanan
 *
 */
public class HarvestFormatter extends Formatter {
  private static String lineFeed = System.getProperty("line.separator", "\n");
  private static String doubleLineFeed = lineFeed + lineFeed;

  private StringBuffer config;
  private StringBuffer summary;

  private int numWarnings;

  private int numErrors;

  public HarvestFormatter() {
    config = new StringBuffer("PDS Harvest Tool Log" + doubleLineFeed);
    summary = new StringBuffer("Summary:" + doubleLineFeed);
    numWarnings = 0;
    numErrors = 0;
  }

  public String format(LogRecord record) {
    if (record instanceof ToolsLogRecord) {
      ToolsLogRecord tlr = (ToolsLogRecord) record;
      StringBuffer message = new StringBuffer();
      if (tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
        return tlr.getMessage() + lineFeed;
      }
      if (tlr.getLevel().intValue() == ToolsLevel.WARNING.intValue()) {
        ++numWarnings;
        ++HarvestSolrStats.numWarnings;
      } else if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
        ++numErrors;
        ++HarvestSolrStats.numErrors;
      }
      if (tlr.getLevel().intValue() != ToolsLevel.CONFIGURATION.intValue()) {
        if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
          message.append("ERROR");
        } else {
          message.append(tlr.getLevel().getName());
        }
        message.append(":   ");
      }
      if (tlr.getFilename() != null) {
        message.append("[" + tlr.getFilename() + "] ");
      }
      if (tlr.getLine() != -1) {
        message.append("line " + tlr.getLine() + ": ");
      }
      message.append(tlr.getMessage());
      message.append(lineFeed);

      return message.toString();
    } else {
      return "******* " + record.getMessage() + " ************" + lineFeed;
    }
  }

  private void processSummary() 
  {
    int totalFiles = HarvestSolrStats.numGoodFiles + HarvestSolrStats.numBadFiles;

    summary.append(HarvestSolrStats.numGoodFiles + " of " + totalFiles
        + " file(s) processed, " + HarvestSolrStats.numFilesSkipped
        + " other file(s) skipped" + lineFeed);
    summary.append(numErrors + " error(s), " + numWarnings + " warning(s)"
        + doubleLineFeed);


    // Registry collection (Labels)
    summary.append("Product Labels:" + lineFeed);
    summary.append(String.format("%-10d %-25s", HarvestSolrStats.numProductsRegistered,
        "Successfully registered"));
    summary.append(lineFeed);
    summary.append(String.format("%-10d %-25s", HarvestSolrStats.numProductsNotRegistered,
        "Failed to register"));
    summary.append(doubleLineFeed);
    
    // Registry Search collection
    summary.append("Registry Search Solr Documents:" + lineFeed);
    summary.append(String.format("%-10d %-25s", HarvestSolrStats.numDocumentsCreated, "Successfully created"));
    summary.append(lineFeed);
    summary.append(String.format("%-10d %-25s", HarvestSolrStats.numDocumentsNotCreated, "Failed to get created"));
    summary.append(doubleLineFeed);
    
    summary.append("Product Types Handled:" + lineFeed);
    for (Entry<String, BigInteger> entry :
      HarvestSolrStats.registeredProductTypes.entrySet()) {
      summary.append(entry.getValue().toString() + " " + entry.getKey()
          + lineFeed);
    }

    int totalGeneratedChecksumsVsManifest =
      HarvestSolrStats.numGeneratedChecksumsSameInManifest
    + HarvestSolrStats.numGeneratedChecksumsDiffInManifest;

    if ( (totalGeneratedChecksumsVsManifest != 0)
        || (HarvestSolrStats.numGeneratedChecksumsNotCheckedInManifest != 0) ) {
      summary.append(lineFeed + HarvestSolrStats.numGeneratedChecksumsSameInManifest
          + " of " + totalGeneratedChecksumsVsManifest
          + " generated checksums matched "
          + "their supplied value in the manifest, "
          + HarvestSolrStats.numGeneratedChecksumsNotCheckedInManifest
          + " value(s) not checked." + lineFeed);
    }

    int totalGeneratedChecksumsVsLabel =
      HarvestSolrStats.numGeneratedChecksumsSameInLabel
      + HarvestSolrStats.numGeneratedChecksumsDiffInLabel;

    if ( (totalGeneratedChecksumsVsLabel != 0)
        || (HarvestSolrStats.numGeneratedChecksumsNotCheckedInLabel != 0) ) {
      summary.append(lineFeed + HarvestSolrStats.numGeneratedChecksumsSameInLabel
          + " of " + totalGeneratedChecksumsVsLabel
          + " generated checksums matched "
          + "the supplied value in their product label, "
          + HarvestSolrStats.numGeneratedChecksumsNotCheckedInLabel
          + " value(s) not checked." + lineFeed);
    }

    int totalManifestChecksumsVsLabel =
      HarvestSolrStats.numManifestChecksumsSameInLabel
      + HarvestSolrStats.numManifestChecksumsDiffInLabel;

    if ( (totalManifestChecksumsVsLabel != 0)
        || (HarvestSolrStats.numManifestChecksumsNotCheckedInLabel != 0) ) {
      summary.append(lineFeed + HarvestSolrStats.numManifestChecksumsSameInLabel
          + " of " + totalManifestChecksumsVsLabel
          + " checksums in the manifest matched "
          + "the supplied value in their product label, "
          + HarvestSolrStats.numManifestChecksumsNotCheckedInLabel
          + " value(s) not checked." + lineFeed);
    }

  }

  public String getTail(Handler handler) {
    StringBuffer report = new StringBuffer("");

    processSummary();

    report.append(lineFeed);
    report.append(summary);
    report.append(doubleLineFeed + "Registry Package Id: " + HarvestSolrStats.packageId);
    report.append(doubleLineFeed + "End of Log" + doubleLineFeed);

    return report.toString();
  }
}
