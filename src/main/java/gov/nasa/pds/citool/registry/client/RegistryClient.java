// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
// $Id$
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
