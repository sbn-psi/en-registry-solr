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
package gov.nasa.pds.citool.registry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metadata 
{
	private Map<String, Values> map;
	
	public Metadata()
	{
		map = new HashMap<>();
	}
	
	public void addMetadata(String key, String value) 
	{
		this.getValues(key).addValue(value);
	}
	
	public String getMetadata(String key) 
	{
		Values vals = map.get(key);
		return vals == null ? null : vals.getValue(); 
	}
	
	public List<String> getAllMetadata(String key) 
	{
		Values vals = map.get(key);
		return vals == null ? null : new ArrayList<String>(vals.getValues());
	}
	
	public boolean containsKey(String key) 
	{
		Values vals = map.get(key);
		return vals != null && !vals.isEmpty();
	}
	
	public boolean isMultiValued(String key) 
	{
		Values vals = map.get(key);
		return vals != null && vals.getValues().size() > 1;
	}

	public List<String> getKeys() 
	{
		return new ArrayList<String>(map.keySet());
	}

	
	private Values getValues(String key)
	{
		Values vals = map.get(key);
		if(vals == null)
		{
			vals = new Values();
			map.put(key, vals);
		}
		
		return vals;
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	
	private static class Values
	{
		private List<String> values;
		
		public Values()
		{
			values = new ArrayList<>();
		}
		
		public void addValue(String value) 
		{
			this.values.add(value);
		}
		
		public String getValue() 
		{
			return values.isEmpty() ? null : values.get(0);
		}
		
		public boolean isEmpty()
		{
			return values.isEmpty();
		}
		
		public List<String> getValues() 
		{
			return this.values;
		}
	}

}
