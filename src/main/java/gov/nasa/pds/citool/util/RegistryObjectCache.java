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
package gov.nasa.pds.citool.util;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.citool.registry.model.RegistryObject;

public class RegistryObjectCache 
{
	private static RegistryObjectCache instance = new RegistryObjectCache();
	
	private Map<String, RegistryObject> map;
	
	private RegistryObjectCache()
	{
		map = new HashMap<>();
	}
	
	public static RegistryObjectCache getInstance()
	{
		return instance;
	}
	
	public void put(RegistryObject ro)
	{
		if(ro == null) return;
		
		// Main LID
		map.put(ro.getLid(), ro);
		
		// Alternative LID (Instrument host can have one, e.g., 
		// main = urn:nasa:pds:context_pds3:instrument_host:spacecraft.go
	    // alternative = urn:nasa:pds:context_pds3:instrument_host:instrument_host.gp)
		if(ro.getAltLid() != null)
		{
			map.put(ro.getAltLid(), ro);
		}
	}
	
	public RegistryObject get(String lid)
	{
		return map.get(lid);
	}
	
	public void clear()
	{
		map.clear();
	}
}
