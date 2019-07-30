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

import static gov.nasa.pds.search.util.RegistryInstallerUtils.print;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;


/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * 
 * Examples taken from:
 * http://www.javased.com/?api=org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *
 */
public class UnzipUtility {

	public static void unzipFile(String archivePath, String targetPath) throws IOException {
		print(" Unzipping " + archivePath + " to " + targetPath);
		File archiveFile = new File(archivePath);
		File targetFile = new File(targetPath);
		ZipFile zipFile = new ZipFile(archiveFile);
		Enumeration<?> e = zipFile.getEntries();
		while (e.hasMoreElements()) {
			ZipArchiveEntry zipEntry = (ZipArchiveEntry)e.nextElement();
			File file = new File(targetFile, zipEntry.getName());
			if (zipEntry.isDirectory()) {
				FileUtils.forceMkdir(file);
			}
			else {
				InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream os = FileUtils.openOutputStream(file);
				try {
					IOUtils.copy(is, os);
				}
				finally {
					os.close();
					is.close();
				}
			}
		}
		zipFile.close();
		print(" Unzip of " + archivePath + " to " + targetPath + " complete.");
	}

}
