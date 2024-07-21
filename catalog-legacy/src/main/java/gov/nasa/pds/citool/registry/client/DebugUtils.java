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
