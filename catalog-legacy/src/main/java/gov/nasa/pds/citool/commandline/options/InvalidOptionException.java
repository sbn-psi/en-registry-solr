// $Id: InvalidOptionException.java 8162 2010-11-10 22:05:09Z mcayanan $
package gov.nasa.pds.citool.commandline.options;

/**
 * Exception class that is called upon errors found during command-line
 * option processing.
 *
 *
 * @author mcayanan
 *
 */
public class InvalidOptionException extends Exception {
    /** Generated serial ID */
    private static final long serialVersionUID = -5439038812448365813L;

    /**
     * Constructor.
     *
     * @param msg An exception message.
     */
    public InvalidOptionException(String msg) {
        super(msg);
    }

}
