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
