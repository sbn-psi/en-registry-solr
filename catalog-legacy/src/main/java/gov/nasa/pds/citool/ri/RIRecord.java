package gov.nasa.pds.citool.ri;

import gov.nasa.pds.tools.LabelParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * An object representation of a referential integrity record.
 *
 * @author mcayanan
 *
 */
public class RIRecord {
    private List<LabelParserException> problems;
    private RIType type;
    private List<String> parentFiles;

    public RIRecord(RIType type, List<String> parentFiles) {
        this.parentFiles = parentFiles;
        this.type = type;
        this.problems = new ArrayList<LabelParserException>();
    }

    public List<String> getParentFiles() {
        return parentFiles;
    }

    public RIType getType() {
       // String filteredString = type.replaceAll("_", " ");
       // return filteredString;
        return type;
    }

    public void addProblem(LabelParserException problem) {
        this.problems.add(problem);
    }

    public void addProblems(List<LabelParserException> problems) {
        this.problems.addAll(problems);
    }

    public List<LabelParserException> getProblems() {
        return problems;
    }

}
