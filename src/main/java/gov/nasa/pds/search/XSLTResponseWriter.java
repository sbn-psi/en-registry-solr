// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.search;

import java.io.IOException;

import javax.xml.transform.Transformer;

import org.apache.solr.request.SolrQueryRequest;

public class XSLTResponseWriter extends
		org.apache.solr.response.XSLTResponseWriter {

	public final static String SOLR_HOME_PARAMETER = "SOLR_HOME";

	@Override
	protected Transformer getTransformer(SolrQueryRequest arg0)
			throws IOException {
		Transformer transformer = super.getTransformer(arg0);

		//System.out.println("********* XSLTResponseWriter     arg0 = " + arg0 +  "        transformer = " + transformer );
		// Set up standard transform parameters
		if (transformer != null
				&& transformer.getParameter(SOLR_HOME_PARAMETER) == null) {
			transformer.setParameter(SOLR_HOME_PARAMETER,
					System.getProperty("solr.pds.home") + "/pds");
		}
		
		//System.out.println("********* XSLTResponseWriter.....SOLR_HOME_PARAMETER = " + transformer.getParameter(SOLR_HOME_PARAMETER));
		return transformer;
	}

}
