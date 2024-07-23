// $Id: ToolsLevel.java 10558 2012-05-25 22:20:10Z mcayanan $
package gov.nasa.pds.harvest.search.logging;

import java.util.logging.Level;

public class ToolsLevel extends Level {
	private static final long serialVersionUID = 2869682506838013252L;
	
	public static final Level CONFIGURATION = new ToolsLevel("CONFIGURATION",
            Level.SEVERE.intValue() + 8);
    public static final Level SKIP = new ToolsLevel("SKIP",
            Level.SEVERE.intValue() + 6);
    public static final Level SUCCESS = new ToolsLevel("SUCCESS",
            Level.SEVERE.intValue() + 3);
    public static final Level NOTIFICATION = new ToolsLevel("NOTIFICATION",
        Level.SEVERE.intValue() + 1);
    public static final Level DEBUG = new ToolsLevel("DEBUG",
        Level.CONFIG.intValue() + 1);

    protected ToolsLevel(String name, int value) {
        super(name, value);
    }
}
