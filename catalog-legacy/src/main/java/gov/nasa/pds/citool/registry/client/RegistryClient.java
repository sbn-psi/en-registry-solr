package gov.nasa.pds.citool.registry.client;

import java.util.List;

import gov.nasa.pds.citool.registry.model.FileInfo;
import gov.nasa.pds.citool.registry.model.RegistryObject;


public interface RegistryClient
{
	public String publishObject(RegistryObject obj) throws Exception;
	public boolean publishFile(FileInfo obj) throws Exception;
	
	public List<String> getResourceIds(String datasetId) throws Exception;
}
