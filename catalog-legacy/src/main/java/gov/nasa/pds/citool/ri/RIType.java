package gov.nasa.pds.citool.ri;

/**
 * Interface containing the different referential integrity
 * types.
 *
 * @author mcayanan
 *
 */
public enum RIType {
    REFERENCE("Reference"),
    PERSONNEL("Personnel"),
    TARGET("Target"),
    INSTRUMENT_HOST("Instrument_Host"),
    INSTRUMENT("Instrument"),
    MISSION("Mission"),
    VOLUME("Volume"),
    DATA_SET("Data_Set"),
    DATA_SET_COLLECTION("Data_set_Collection");

    public final static String PARENT = "Parent";
    public final static String CHILD = "Child";
    private final String name;

    private RIType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
