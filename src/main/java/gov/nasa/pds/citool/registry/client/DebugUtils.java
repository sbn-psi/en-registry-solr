// Copyright 2009-2023, by the California Institute of Technology.
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
import java.util.Map;

import gov.nasa.pds.citool.registry.model.RegistryObject;


public class DebugUtils 
{
	public static void print(RegistryObject ro, int maxLen)
	{
		for(Map.Entry<String, List<String>> item: ro.getSlots().entrySet())
		{
			System.out.print(item.getKey() + " = ");
			List<String> vals = item.getValue(); 
			if(vals.size() == 1)
			{
				System.out.println(truncate(vals.get(0), maxLen));
			}
			else
			{
				System.out.print("[");
				for(String val: vals)
				{
					System.out.print(truncate(val, maxLen) + " | ");
				}
				System.out.println("]");
			}
		}
	}
	
	private static String truncate(String str, int maxLen)
	{
		if(str == null) return "null";
		return (str.length() > maxLen) ? str.substring(0, maxLen) : str;
	}
}
