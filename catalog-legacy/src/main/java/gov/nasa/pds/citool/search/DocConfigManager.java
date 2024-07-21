package gov.nasa.pds.citool.search;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.search.core.schema.CoreConfigReader;
import gov.nasa.pds.search.core.schema.Product;
import gov.nasa.pds.search.core.schema.Query;


public class DocConfigManager 
{
	private static DocConfigManager instance;

	private Map<String, Product> configMap;
	
	
	private DocConfigManager(Map<String, Product> configMap)
	{
		this.configMap = configMap;
	}
	
	
	public static DocConfigManager getInstance()
	{
		return instance;
	}
	
	
	public static void init(String configDir) throws Exception
	{
		instance = null;
		
		if(configDir == null)
		{
			throw new Exception("Invalid configuration directory.");
		}
		
		File root = new File(configDir);
		if(!root.isDirectory())
		{
			throw new Exception("Not a directory: " + configDir);
		}
		
		Collection<File> files = FileUtils.listFiles(root, new String[] { "xml" }, true);
		
		Map<String, Product> configMap = new HashMap<String, Product>();
		
		for(File file: files)
		{
			Product product = CoreConfigReader.unmarshall(file);

			for(Query query: product.getSpecification().getQuery())
			{
				String registryPath = query.getRegistryPath();
				if("objectType".equalsIgnoreCase(registryPath)) 
				{
					configMap.put(query.getValue().toLowerCase(), product);
				}
			}
		}
		
		instance = new DocConfigManager(configMap);
	}
	
	
	public Product getConfigByObjectType(String type)
	{
		if(type == null) return null;
		
		type = type.toLowerCase();
		return configMap.get(type);
	}
	
}
