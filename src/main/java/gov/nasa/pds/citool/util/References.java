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
