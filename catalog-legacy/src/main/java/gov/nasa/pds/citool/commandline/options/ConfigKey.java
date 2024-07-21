package gov.nasa.pds.citool.commandline.options;

public enum ConfigKey {
    MODE("citool.mode"),
    TARGET("citool.targets"),
    LOCAL("citool.local"),
    REPORT("citool.report"),
    DICTIONARIES("citool.dictionaries"),
    INCLUDES("citool.includePaths"),
    VERBOSE("citool.verbose"),
    USER("citool.user"),
    PASS("citool.pass"),
    SERVERURL("citool.serverUrl"),
    TRANSPORTURL("citool.transportUrl"),
    KEYPASS("citool.keypass"),
    ALLREFS("citool.allrefs"),
    ALIAS("citool.alias");


    private final String key;

    private ConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
