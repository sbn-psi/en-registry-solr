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
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;

public class DocWriter 
{
	private File outDir;
	private FileWriter writer;	
	
	public DocWriter(String outDir, String volumeId) throws Exception
	{
		this.outDir = new File(outDir);
		this.outDir.mkdirs();
		
		File file = new File(this.outDir, volumeId + ".solr.xml");
		writer = new FileWriter(file);
		writer.write("<add>\n");
	}
	
	public void close() throws Exception
	{
		writer.write("</add>\n");
		writer.close();
	}
	
	public void write(Map<String, List<String>> fields) throws Exception
	{
		writer.write("<doc>\n");
		writer.write("<field name=\"package_id\">" + UUID.randomUUID().toString() + "</field>\n");
		
		for(Map.Entry<String, List<String>> field: fields.entrySet())
		{
			String fieldName = field.getKey();
			for(String value: field.getValue())
			{
				writer.write("<field name=\"" + fieldName + "\">");
				StringEscapeUtils.escapeXml(writer, value);
				writer.write("</field>\n");
			}
		}
		
		writer.write("</doc>\n");
	}
}
