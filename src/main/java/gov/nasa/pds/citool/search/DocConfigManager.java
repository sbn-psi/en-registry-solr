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
