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
import java.util.Set;

public class Slots
{
	private Map<String, List<String>> map;
	
	public Slots()
	{
		map = new HashMap<String, List<String>>();
	}
	
	public void put(String key, String value)
	{
		List<String> list = new ArrayList<String>(1);
		list.add(value);
		map.put(key, list);
	}

	
	public void put(String key, List<String> values)
	{
		map.put(key, values);
	}

	
	public Set<Map.Entry<String, List<String>>> entrySet()
	{
		return map.entrySet();
	}

	public List<String> get(String key)
	{
		return map.get(key);
	}
	
	public String getFirst(String key)
	{
		List<String> list = map.get(key);
		return (list == null || list.isEmpty()) ? null : list.get(0);
	}
	
	public boolean containsKey(String key)
	{
		return map.containsKey(key);
	}
}
