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

public class References 
{
	private static References instance = new References();
	private Map<String, String> refs;
		
	private References()
	{
		refs = new HashMap<String, String>();
	}
	
	public static References getInstance()
	{
		return instance;
	}

	public void put(String key, String value)
	{
		refs.put(key, value);
	}
	
	public String get(String key)
	{
		String val = refs.get(key);
		return (val == null) ? key : val;
	}
	
	public void clear()
	{
		refs.clear();
	}
}
