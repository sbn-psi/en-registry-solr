package gov.nasa.pds.harvest.search.util;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Class that handles errors while parsing an XML file.
 *
 * @author mcayanan
 *
 */
public class XMLErrorListener implements ErrorListener {

    /**
     * Method is called when an error is encountered.
     *
     * @param exception The exception containing the error.
     *
     * @throws TransformerException Throws the exception.
     */
    @Override
    public void error(TransformerException exception)
    throws TransformerException {
        throw new TransformerException(exception);
    }

    /**
     * Method is called when a fatal error is encountered.
     *
     * @param exception The exception containing the fatal error.
     *
     * @throws TransformerException Throws the exception.
     */
    @Override
    public void fatalError(TransformerException exception)
        throws TransformerException {
      throw new TransformerException(exception);

    }

    /**
     * Method is called when a warning is encountered.
     *
     * @param exception The exception containing the warning.
     *
     * @throws TransformerException Throws the exception.
     */
    @Override
    public void warning(TransformerException exception)
    throws TransformerException {
      throw new TransformerException(exception);
    }

}
