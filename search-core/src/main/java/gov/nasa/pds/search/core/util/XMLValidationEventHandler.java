package gov.nasa.pds.search.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;

public class XMLValidationEventHandler implements ValidationEventHandler {
    private static Logger log = Logger.getLogger(
            XMLValidationEventHandler.class.getName());
    private String systemId;

    public XMLValidationEventHandler(String systemId) {
      this.systemId = systemId;
    }

    public boolean handleEvent(ValidationEvent event) {
        Level level = null;
        if(event.getSeverity() == ValidationEvent.ERROR
                || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            level = ToolsLevel.SEVERE;
        } else if(event.getSeverity() == ValidationEvent.WARNING) {
            level = ToolsLevel.WARNING;
        }
        if (event.getLocator().getURL() != null) {
          log.log(new ToolsLogRecord(level, event.getMessage(),
                event.getLocator().getURL().toString(),
                event.getLocator().getLineNumber()));
        } else {
          log.log(new ToolsLogRecord(level, event.getMessage(),
              systemId, event.getLocator().getLineNumber()));
        }
        return false;
    }

}
