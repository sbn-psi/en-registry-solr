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

package gov.nasa.pds.search.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InstallerPresets extends Properties {

	private static final long serialVersionUID = 1L;
	private String presetsFilePath;

	public InstallerPresets() {
		super();
		InputStream propStream = null;
		try {
			presetsFilePath = System.getenv("REGISTRY_INSTALLER_PRESET_FILE");
			propStream = new FileInputStream(new File (presetsFilePath));
			this.load(propStream);
			//System.out.println(this.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (propStream != null)
					propStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public InstallerPresets(Properties defaults) {
		super(defaults);
	}
	
	public String getPresetsFilePath() {
		return presetsFilePath;
	}
	
	public void writeOutToFile() {
		System.out.println("Writing configuration properties to: " + presetsFilePath);
		try {
			File file = new File(presetsFilePath);
			file.delete();
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			this.store(fileOut, "REGISTRY Configuration Properties (see: https://wiki.jpl.nasa.gov/display/search/Configuring+SEARCH)" + System.currentTimeMillis());
			fileOut.close();
			
			File nextVer = new File(presetsFilePath);
			String preContents = RegistryInstallerUtils.getFileContents(nextVer.toPath());
			String nextContents = preContents.replaceAll("\\\\", "");
			RegistryInstallerUtils.writeToFile(nextVer.toPath(), nextContents);
		}
		catch (Throwable t) {
			System.out.println("ERROR: Problem writing out properties to : " + presetsFilePath);
		}
	}
}
