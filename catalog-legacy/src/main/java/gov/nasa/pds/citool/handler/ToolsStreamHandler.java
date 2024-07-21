package gov.nasa.pds.citool.handler;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

/**
 * This class sets up a stream handler for the tools logging capability.
 * 
 * @author mcayanan
 *
 */
public class ToolsStreamHandler extends StreamHandler {
	
	/**
	 * Constructor. Automatically sets the log level to 'ALL'.
	 * 
	 * @param out An output stream.
	 * @param formatter Formatter to be used to format the log messages.
	 */
	public ToolsStreamHandler(OutputStream out, Formatter formatter) {
		this(out, Level.ALL, formatter);
	}
	
	/**
	 * Constructor.
	 * @param out An output stream.
	 * @param level Sets the log level, specifying which message levels will
	 * be logged by this handler.
	 * @param formatter Formatter to be used to format the log messages.
	 */
	public ToolsStreamHandler(OutputStream out, Level level, Formatter formatter) {
		super(out, formatter);
		setLevel(level);
	}
}
