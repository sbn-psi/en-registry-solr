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
