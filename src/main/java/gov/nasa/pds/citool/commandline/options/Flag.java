// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id: Flag.java 8162 2010-11-10 22:05:09Z mcayanan $
package gov.nasa.pds.citool.commandline.options;

import org.apache.commons.cli.Options;

/**
 * Class that holds the command-line option flags.
 *
 * @author mcayanan, hyunlee
 *
 */
public enum Flag {
    /** Flag to enable aliasing. */
    ALIAS("a", "alias", "Enable aliasing."),

    /** Flag to specify the allrefs dictionary support file. */
    ALLREFS("A", "allrefs", "file", String.class, "Specify the allrefs "
            + "dictionary support file or URL."),

    /** Specify a configuration file. */
    CONFIG("c", "configuration", "file", String.class, "Specify a "
            + "configuration file to change tool behavior."),

    DOC_CONFIG("C", "doc-config", "dir", String.class, 
    		"Specify the directory location where the document generation configuration files reside."),
    
    /** Specify the PDS data dictionary files. */
    PDSDD("d", "dict", ".full file(s)", String.class, true, "Specify the "
            + "PDS data dictionary file(s)."),

    /** Flag to specify the tool mode. */
    MODE("m", "mode", "function", String.class, "Specify the mode to run "
            + "the tool. Valid values are 'compare', 'ingest', or "
            + "'validate'."),

    /** Flag to specify the path(s) to search for file references. */
    INCLUDES("I", "include", "paths", String.class, true, "Specify the "
            + "path(s) to search for files referenced in pointer statements. "
            + "Default is to search for the referenced files in the same "
            + "directory as the catalog file."),

    /** Flag to not traverse a directory. */
    LOCAL("L", "local", "Do not recurisevly traverse down a target "
            + "directory."),

    OUTPUT_DIR("o", "output-dir", "dir", String.class, 
    		"Specify a directory location to tell the tool "
    		+ "where to output the Solr documents. The default is "
    		+ "to write to the current working directory."),
    
    /** Flag to explicitly specify the targets. */
    TARGET("t", "target", "catalogs", String.class, true, "Explicitly specify "
            + "the catalog target(s). Targets can be specified implicitly as "
            + "well."),

    /** Flag to specify the report file. */
    REPORT("r", "report-file", "file name", String.class, "Specify the report "
            + "file name. Default is standard out."),

    /** Flag to specify the severity level. */
    VERBOSE("v", "verbose", "1|2|3", short.class, "Specify the severity "
            + "level and above to include in the report: (1=info, 2=warning,"
            + "3=error. Default is Warnings and above (level 2)."),

    /** Flag to display the help. */
    HELP("h", "help", "Display usage."),

    /** Flag to display the version. */
    VERSION("V", "version", "Display application version.");

    /** The short name of the flag. */
    private final String shortName;

    /** The long name of the flag. */
    private final String longName;

    /** An argument name for the flag, if it accepts argument values. */
    private final String argName;

    /** The type of argument values the flag accepts. */
    private final Object argType;

    /** A boolean value indicating if the flag accepts more than one
     * argument.
     */
    private final boolean allowsMultipleArgs;

    /** The flag description. */
    private final String description;

    /** A list of Option objects for command-line processing. */
    private static Options options;

    /**
     * Constructor.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param description A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String description) {
        this(shortName, longName, null, null, description);
    }

    /**
     * Constructor for flags that can take arguments.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param argName The argument name.
     * @param argType The argument type.
     * @param description A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String argName, final Object argType,
            final String description) {
        this(shortName, longName, argName, argType, false, description);
    }

    /**
     * Constructor for flags that can take arguments.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param argName The argument name.
     * @param argType The argument type.
     * @param description A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String argName, final Object argType,
            final boolean allowsMultipleArgs, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.argName = argName;
        this.argType = argType;
        this.allowsMultipleArgs = allowsMultipleArgs;
        this.description = description;
    }

    /**
     * Get the short name of the flag.
     *
     * @return The short name.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Get the long name of the flag.
     *
     * @return The long name.
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Get the argument name of the flag.
     *
     * @return The argument name.
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Find out if the flag can handle multiple arguments.
     *
     * @return 'true' if yes.
     */
    public boolean allowsMultipleArgs() {
        return allowsMultipleArgs;
    }

    /**
     * Get the argument type of the flag.
     *
     * @return The argument type.
     */
    public Object getArgType() {
        return argType;
    }

    /**
     * Get the flag description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    static {
        options = new Options();

        options.addOption(new ToolsOption(ALIAS));
        options.addOption(new ToolsOption(ALLREFS));
        options.addOption(new ToolsOption(CONFIG));
        options.addOption(new ToolsOption(DOC_CONFIG));
        options.addOption(new ToolsOption(MODE));
        options.addOption(new ToolsOption(INCLUDES));
        options.addOption(new ToolsOption(LOCAL));
        options.addOption(new ToolsOption(OUTPUT_DIR));
        options.addOption(new ToolsOption(PDSDD));
        options.addOption(new ToolsOption(TARGET));
        options.addOption(new ToolsOption(REPORT));
        options.addOption(new ToolsOption(VERBOSE));
        options.addOption(new ToolsOption(HELP));
        options.addOption(new ToolsOption(VERSION));
    }

    /**
     * Get the command-line options.
     *
     * @return A class representation of the command-line options.
     */
    public static Options getOptions() {
        return options;
    }
}
