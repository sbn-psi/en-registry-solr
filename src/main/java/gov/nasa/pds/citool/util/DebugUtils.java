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

import java.util.List;
import java.util.Map;

public class DebugUtils 
{
	public static void printRefs(Map<String, List<String>> refs)
	{
		if(refs == null) 
		{
			System.out.println("Refs i snull.");
			return;
		}
		
		for(Map.Entry<String, List<String>> item: refs.entrySet())
		{
			String key = item.getKey();
			System.out.println(key);
			
			List<String> vals = item.getValue();
			for(String val: vals)
			{
				System.out.println("    " + val);
			}
		}
	}
}
