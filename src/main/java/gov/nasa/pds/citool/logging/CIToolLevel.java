package gov.nasa.pds.citool.logging;

import java.util.logging.Level;

import gov.nasa.pds.citool.logging.ToolsLevel;

public class CIToolLevel extends ToolsLevel {
	public static final Level DEBUG = new CIToolLevel("DEBUG", Level.FINEST.intValue() + 1);
	public static final Level INFO_NOTIFY = new CIToolLevel("INFO_NOTIFY", Level.INFO.intValue() + 1);
	public static final Level WARNING_NOTIFY = new CIToolLevel("WARNING_NOTIFY", Level.WARNING.intValue() + 1);
	public static final Level SEVERE_NOTIFY = new CIToolLevel("SEVERE_NOTIFY", Level.SEVERE.intValue() + 4);
	public static final Level DIFF = new CIToolLevel("DIFF", Level.SEVERE.intValue() + 5);

	protected CIToolLevel(String name, int value) {
		super(name, value);
	}
}
