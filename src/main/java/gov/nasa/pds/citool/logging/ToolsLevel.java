// $Id: ToolsLevel.java 5853 2010-02-06 22:54:00Z shardman $
//

package gov.nasa.pds.citool.logging;

import java.util.logging.Level;

/**
 * @author pramirez
 * @version $Revision: 5853 $
 * 
 */
public class ToolsLevel extends Level {
	
	public static final Level CONFIGURATION = new ToolsLevel("CONFIGURATION",
            Level.SEVERE.intValue() + 9);	
	public static final Level PARAMETER = new ToolsLevel("PARAMETER",
            Level.SEVERE.intValue() + 8);
    public static final Level INGEST_ASSOC_SKIP = new ToolsLevel("SKIP",
            Level.SEVERE.intValue() + 7);
    public static final Level SKIP = new ToolsLevel("SKIP",
            Level.SEVERE.intValue() + 6);
    public static final Level INGEST_FAIL = new ToolsLevel("FAILURE",
            Level.SEVERE.intValue() + 5);
    public static final Level INGEST_ASSOC_FAIL = new ToolsLevel("FAILURE",
            Level.SEVERE.intValue() + 4);
    public static final Level INGEST_SUCCESS = new ToolsLevel("SUCCESS",
            Level.SEVERE.intValue() + 3);
    public static final Level INGEST_ASSOC_SUCCESS = new ToolsLevel("SUCCESS",
            Level.SEVERE.intValue() + 2);
    public static final Level NOTIFICATION = new ToolsLevel("NOTIFICATION",
            Level.SEVERE.intValue() + 1);
    
    protected ToolsLevel(String name, int value) {
        super(name, value);
    }

}
