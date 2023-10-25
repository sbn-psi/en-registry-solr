package gov.nasa.pds.citool.ingestor;

import java.io.File;
import java.io.FilenameFilter;

public class CatalogFilenameFilter implements FilenameFilter
{
    String extension = null;
    public CatalogFilenameFilter(String extension)
    {
        this.extension = extension;
    }

    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(this.extension.toLowerCase());
    }
}
