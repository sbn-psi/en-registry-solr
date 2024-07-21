package gov.nasa.pds.citool.registry.client;

public class RegistryClientManager 
{
	private static RegistryClient client;
	
	public static RegistryClient getRegistryClient()
	{
		return client;
	}
	
	public static void init(String registryUrl) throws Exception
	{
		client = new RegistryClientSolr(registryUrl);
	}

}
