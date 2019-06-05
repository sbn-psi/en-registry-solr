package gov.nasa.pds.search;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import gov.nasa.pds.search.util.InstallerPresets;
import gov.nasa.pds.search.util.RegistryInstallerUtils;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.bailOutMissingOption;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.bailOutWithMessage;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.copy;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.copyAll;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.copyAllType;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.copyDir;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.createFile;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.deleteDirectory;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.flushConsole;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.getFileContents;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.getPreset;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.getInstallerPresets;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.isLocalPortAvailable;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.isRemotePortListening;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.openUpPermissions;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.print;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.readLine;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.readPassword;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.readRequiredLine;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.serverListening;
import static gov.nasa.pds.search.util.RegistryInstallerUtils.writeToFile;
import static gov.nasa.pds.search.util.UnzipUtility.unzipFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.lang.reflect.Field;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Scanner;
import java.util.logging.Level;

import javax.tools.ToolProvider;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;

//import com.sun.management.OperatingSystemMXBean;

/*
 * @author hyunlee
 *
 */
public class RegistryInstaller {
	private static final Logger log = LoggerFactory.getLogger(RegistryInstaller.class);

	private static final String SEP = File.separator;

	// Number of bytes per gigabyte
	public static final long BYTES_PER_GIG = (long)1e+9;
	private static String solrCmd = "solr";
	private static String dockerCmd = "deploy-docker.sh";
	private static String envStr = null;
	private static String prompt = null;

	private static String osName;
	private static String termName = null;
	private static int physicalMemoryGigs;
	private static int solrPort = 8983;
	private static String solrHost = "localhost";

	private static String registry_version;
	private static String registry_root;
	private static String registry_solr_root;
	private static String registry_solr_bin;
	private static String registry_solr_conf;
	private static String registry_solr_lib;
	private static String registry_docker_build;

	private static String installType;

	private static boolean docker_mode = false;
	private static String harvest_home = ""; 
	private static boolean delete = false;

	private static int maxShardsPerNode = 2;
	private static int numShards = 1;
	private static int replicationFactor = 1;

	public RegistryInstaller() {}

	public static void main(String args[]) {
		for (String arg : args) {
            if (arg.equals("--delete") || arg.equals("-d")) {
                delete = true;
                break;
            }

        }
		Scanner reader = new Scanner(System.in);  // Reading from System.in
//		System.out.print("Enter location of Harvest installation: ");
//		harvest_home = reader.next();

		System.out.print("Enter an installation mode (docker or standalone): ");
		installType = reader.next(); // Scans the next token of the input as a string.			

//		System.out.println("Harvest installation location -  " + harvest_home);

		if (installType.equalsIgnoreCase("docker"))
			docker_mode = true;

		Path currentRelativePath = Paths.get("");
		registry_root = currentRelativePath.toAbsolutePath().toString() + SEP + "..";
		//System.out.println("Current relative path is (SEARCH_HOME): " + registry_root);

		print("STARTING Registry Installer in " + installType + " mode.");
		getVersion();
		getOsName();
		getTerminal();
		getSolrPort();
		getSolrHost();

		try {
			printWelcomeMessage();
		} catch (Exception ex) {
			ex.printStackTrace();
		}	

		if (!docker_mode) {  // standalone mode
			System.out.print("Enter location of SOLR installation: ");
			registry_solr_root = reader.next();

			init();
			if (osName.contains("Windows")) {
				solrCmd = "solr.cmd";
			}

			if (delete) {
				deleteRegistrySearchCollection();
				stopSOLRServer();
				exit(1); // to test for now.
			}

			// copy search service confs, *jar and lib into SOLR directories
			setupSOLRDirs();
			startSOLRServer();

			maxShardsPerNode = Integer.parseInt(getPreset("maxShardsPerNode"));
			numShards = Integer.parseInt(getPreset("numShards"));
			replicationFactor = Integer.parseInt(getPreset("replicationFactor"));

			// Create '.system' collection
			createRegistryCollection();

			// Create 'xpath' collection
			createRegistryXpathCollection();

			// Create 'pds' collection
			createSearchCollection();
		}
		else {
			registry_docker_build = registry_root+SEP+"build";
			prompt = "noPrompt";
            //print("termName = " + termName);
			if (osName.contains("Windows") && termName.contains("cygwin")) {
				envStr = "bash";
				//print("envStr = " + envStr);
				String tmpCmd = FilenameUtils.separatorsToUnix(SEP + registry_docker_build+SEP+dockerCmd);
				print("tmpCmd = " + tmpCmd);
				dockerCmd = tmpCmd.replace(":", "");
				dockerCmd = FilenameUtils.normalize(dockerCmd, true);
				//print("dockerCmd = " + dockerCmd);
			}
			else {
				dockerCmd = FilenameUtils.separatorsToSystem(registry_docker_build+SEP+dockerCmd);
				//print("dockerCmd = " + dockerCmd);
			}
			
			if (delete) {				
				executeDockerBuild("uninstall", envStr);
			}
			else 
				executeDockerBuild("install", envStr);
		}
		reader.close();
	}

	private static void init() {
		registry_solr_bin  = registry_solr_root+SEP+"bin";
		registry_solr_conf = registry_solr_root+SEP+"server"+SEP+"solr"+SEP+"configsets"+SEP+"pds"+SEP+"conf";
		registry_solr_lib  = registry_solr_root+SEP+"contrib"+SEP+"pds"+SEP+"lib";
	}

	private static void exit(int status) {
		System.exit(status);
	}

	private static void getVersion() {
		//System.out.println("getenv(REGISTRY_VER) = " + getenv("REGISTRY_VER"));
		registry_version = getenv("REGISTRY_VER");

	}

	private static void getOsName() {
		osName = getProperty("os.name");
	}

	private static void getTerminal() {
		if (System.getenv("TERM")!=null)
			termName = System.getenv("TERM");
	}

	private static void getSolrPort() {
		solrPort = Integer.parseInt(getPreset("solr.port"));
	}

	private static void getSolrHost() {
		solrHost = getPreset("solr.host");
	}

	private static void printWelcomeMessage() throws Exception {
		print ("");
		print ("  Registry   .....   ");
		print ("       ( v " + registry_version + " )");
		print ("       ( installing on platform: " + osName + " )");
		InetAddress inetAddress = InetAddress.getLocalHost();
        print ("       ( IP Address:- " + inetAddress.getHostAddress() + " )");
        print ("       ( Host Name:- " + inetAddress.getHostName() + " )");
        print ("");
	}

	private static void setupSOLRDirs() {
        try {
			// Copy the Search API JAR to necessary location for Solr to recogize it
			copyDir(registry_root+SEP+"dist", registry_solr_lib);

			// Copy 'pds' config set to necessary location for Solr to recognize it
			copyDir(registry_root+SEP+"conf", registry_solr_conf);

			// Copy saxon*.jar to solr-webapp directory
			String solr_webapp_dir = registry_solr_root+SEP+"server"+SEP+"solr-webapp"+SEP+"webapp"+SEP+"WEB-INF"+SEP+"lib";
			copy(Paths.get(registry_root+SEP+"lib"+SEP+"saxon-9.jar"),
				Paths.get(solr_webapp_dir+SEP+"saxon-9.jar"));
			copy(Paths.get(registry_root+SEP+"lib"+SEP+"saxon-dom-9.jar"),
				Paths.get(solr_webapp_dir+SEP+"saxon-dom-9.jar"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void startSOLRServer() {		
        Process progProcess = null;
        int returnVal = -1;
		try {
			// need to check wheather the SOLR server is running already or not
			// ./solr status       -> "No Solr nodes are running."
			String[] statusCmd = new String[] {registry_solr_bin+SEP+solrCmd, "status", "-p", String.valueOf(solrPort)};
			progProcess = Runtime.getRuntime().exec(statusCmd);
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            String line = null;
            boolean runningProc = false;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                if (line.contains("running on port " + solrPort)) {
                	runningProc = true;
                	System.out.println(line);
                }
            }

			try{
               	returnVal = progProcess.waitFor();
               	//print("Return status from the SOLR server status = " + returnVal);

               	if (runningProc) {
               		print("Failed to start the SOLR server. There is already RUNNING SOLR instance.");
               		exit(1);
           		}
            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();
            print("\nStarting a SOLR server ....Waiting up to 180 seconds to see Solr running on port " + solrPort + "....");

            //./bin/solr start -c -a "-Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl \
            // -Dsolr.pds.home=$SOLR_HOME/server/solr/ --Xmx2048m" -s $SOLR_HOME/server/solr
			String sysProperties = "-Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl " + 
				"-Dsolr.pds.home=" + registry_solr_root + SEP + "server" + SEP + "solr -Xmx2048m";
			     //"-Dsolr.pds.home=" + registry_solr_root + SEP + "server" + SEP + "solr";
			      
			String[] execCmd = new String[] { registry_solr_bin + SEP + solrCmd, "start", "-c",
		        	"-p", String.valueOf(solrPort), "-a", sysProperties, "-s", registry_solr_root + SEP + "server" + SEP + "solr"};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);		
			in = new BufferedReader(new InputStreamReader(progProcess.getInputStream()));
            
            // SOLR starup hangs for some reason
            // Sleep for x seconds instead of calling waitFor() on Windows platform
            if (osName.contains("Windows")) {
            	Thread.sleep(Integer.parseInt(getPreset("waitTime"))*1000);
            	print("SOLR server is started on " + osName);
            }
            else { 
            	while ((line = in.readLine()) != null) {
                	System.out.println(line);
            	}
            	try{
               		returnVal = progProcess.waitFor();
               		print("Return status from the SOLR server startup = " + returnVal);
               		if (returnVal!=0) {
                  		print("Failed to start the SOLR server....");
                  		exit(1);
               		}
               		else 
               			print("The SOLR server is started successfully.");
            	} catch(InterruptedException ie){
               		ie.printStackTrace();
            	} 
            }       
           	in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void stopSOLRServer() {		
        Process progProcess = null;
        int returnVal = -1;
		try {
			// need to check wheather the SOLR server is running already or not
			// ./solr status       -> "No Solr nodes are running."
			progProcess = Runtime.getRuntime().exec(new String[] { registry_solr_bin + SEP + solrCmd, "status", "-p", String.valueOf(solrPort)});
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            String line = null;
            boolean runningProc = false;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.contains("running on port")) {
                	//print("Failed to start the SOLR server. There is already RUNNING SOLR instance.");
                	runningProc = true;
                }
            }
			try{
               	returnVal = progProcess.waitFor();
               	//print("Return status from the SOLR server status = " + returnVal);
          		if (!runningProc) {
               		print("There is no RUNNING SOLR instance. No need to stop the SOLR server.");
               		exit(1);
           		}
            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();

            print("\nStopping the SOLR server...");

            //./bin/solr stop
			String[] execCmd = new String[] { registry_solr_bin + SEP + solrCmd, "stop", "-p", String.valueOf(solrPort)};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);		
			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	//print("Return status from the SOLR server stop = " + returnVal);
               	if (returnVal!=0) {
                  	print("Failed to stop the SOLR server....");
                  	exit(1);
               	}
               	else
               		print("The SOLR server is stopped successfully.");
            } catch(InterruptedException ie){
               	ie.printStackTrace();
            }    
           	in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void createSearchCollection() {
		print("Creating Search collection (pds) ...");
		Process progProcess = null;
        int returnVal = -1;
		try {
			String[] execCmd = new String[] { registry_solr_bin+SEP+solrCmd, "create", 
					"-p", String.valueOf(solrPort), "-c", "pds", "-d", "pds"};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");
					
			progProcess = Runtime.getRuntime().exec(execCmd);
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
			String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	print("Return status from creating search collection = " + returnVal);
               	if (returnVal!=0) {
                	print("Failed to create a search collection....");
                  	exit(1);
               	}
               	else 
               		print("Search collection (pds) is created successfully.");
            } catch(InterruptedException ie){
               	ie.printStackTrace();
            }
			in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void createRegistryXpathCollection() {
		print("Creating Registry Service collection (xpath) ...");
		Process progProcess = null;
        int returnVal = -1;
		try {
			//curl "http://localhost:8983/solr/admin/collections?action=CREATE&name=xpath&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}
			String execCmd = "curl http://"+solrHost+":"+solrPort+"/solr/admin/collections?action=CREATE&name=xpath" + 
					"&maxShardsPerNode=" + maxShardsPerNode + "&numShards=" + numShards + "&replicationFactor=" + replicationFactor;
			//print("Executing '" + execCmd + "'");
			progProcess = Runtime.getRuntime().exec(execCmd);

			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
			String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	//print("Return status from creating registry service collection (xpath) = " + returnVal);
               	if (returnVal!=0) {
                  	print("Failed to create a registry service collection (xpath) ....");
                  	exit(1);
               	}
               	else 
               		print("Registry service collection (xpath) is created successfully.");
            } catch(InterruptedException ie){
         		ie.printStackTrace();
            }
            in.close();

            //curl "http://localhost:8983/solr/admin/collections?action=CREATEALIAS&name=registry-xpath&collections=xpath"
            execCmd = "curl http://"+solrHost+":"+solrPort+"/solr/admin/collections?action=CREATEALIAS&name=registry-xpath&collections=xpath";
			//print("Executing '" + execCmd + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);
			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	//print("Return status from creating registry service collection alias = " + returnVal);
               	if (returnVal!=0) {
                	print("Failed to create a registry service collection alias (xpath) ....");
                  	exit(1);
               	}
               	else 
               		print("Registry service collection alias (xpath) is created successfully.");
            } catch(InterruptedException ie){
               	ie.printStackTrace();
            }
            in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void createRegistryCollection() {
		print("Creating Registry Service collection (.system) ...");
		Process progProcess = null;
        int returnVal = -1;
		try {
			//curl "http://localhost:8983/solr/admin/collections?action=CREATE&name=.system&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}	
			String execCmd = "curl http://"+solrHost+":"+solrPort+"/solr/admin/collections?action=CREATE&name=.system" + 
					"&maxShardsPerNode=" + maxShardsPerNode + "&numShards=" + numShards + "&replicationFactor=" + replicationFactor;
			//print("Executing '" + execCmd + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
			String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            try{
            	returnVal = progProcess.waitFor();
                print("Return status from creating registry service collection (.system) = " + returnVal);
                if (returnVal!=0) {
                    print("Failed to create a registry service collection (.system) ....");
                    exit(1);
                }
                else 
                	print("Registry service collection (.system) is created successfully.");
            } catch(InterruptedException ie){
            	ie.printStackTrace();
            }
            in.close();

            execCmd = "curl http://"+solrHost+":"+solrPort+"/solr/admin/collections?action=CREATEALIAS&name=registry-blob&collections=.system";
			//print("Executing '" + execCmd + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);

			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	print("Return status from creating registry service collection alias = " + returnVal);
               	if (returnVal!=0) {
                	print("Failed to create a registry service collection alias (.system) ....");
                  	exit(1);
               	}
               	else 
               		print("Registry service collection alias (.system) is created successfully.");
            } catch(InterruptedException ie){
            	ie.printStackTrace();
            }
			in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void deleteRegistrySearchCollection() {
		print("Deleting Registry and Search collections (.system, xpath, pds) ....");
		Process progProcess = null;
        int returnVal = -1;
		try {
			String[] execCmd = new String[]{"curl", "http://"+solrHost+":"+solrPort+
				"/solr/admin/collections?action=DELETEALIAS&name=registry-blob&collection=.system"};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                print(line);
            }

            try{
               	returnVal = progProcess.waitFor();
               	print("Return status from deleting registry service collection alias (.system) = " + returnVal);
               	if (returnVal!=0) {
                	print("Failed to delete a registry service collection alias (.system).");
                  	exit(1);
               	}
               	else 
               		print("Registry service collection alias (.system) is deleted successfully.");
            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();

            execCmd = new String[]{"curl", "http://"+solrHost+":"+solrPort+
            	"/solr/admin/collections?action=DELETEALIAS&name=registry-xpath&collections=xpath"};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");
			progProcess = Runtime.getRuntime().exec(execCmd);

			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                print(line);
            }
            try{
               	returnVal = progProcess.waitFor();
               	print("Return status from deleting registry service collection alias (xpath) = " + returnVal);
               	if (returnVal!=0) {
                  	print("Failed to delete a registry service collection alias (xpath).");
               	   exit(1);
               	}
               	else 
               		print("Registry service collection alias (xpath) is deleted successfully.");
            } catch(Exception ex){
            	ex.printStackTrace();
            }
            in.close();

            // .system collection
            execCmd = new String[] { registry_solr_bin+SEP+solrCmd, "delete", "-p", String.valueOf(solrPort), "-c", ".system"};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");
			progProcess = Runtime.getRuntime().exec(execCmd);
			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
			try{
               	returnVal = progProcess.waitFor();
               	print("Return status from deleting registry service collection (.system) = " + returnVal);

               	if (returnVal!=0) {
                  	print("Failed to delete a registry service collection alias (.system).");
                  	exit(1);
               	}
               	else
               		print("Registry service collection (.system) is deleted successfully.");
            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();

            execCmd = new String[] { registry_solr_bin+SEP+solrCmd, "delete", "-p", String.valueOf(solrPort), "-c", "xpath",};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");
			progProcess = Runtime.getRuntime().exec(execCmd);
			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
			try{
               	returnVal = progProcess.waitFor();
               	if (returnVal!=0) {
                  	print("Failed to delete a registry service collection (xpath).");
                  	exit(1);
                }
                else 
                	print("Registry service collection (xpath) is deleted successfully.");
            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();

            execCmd = new String[] { registry_solr_bin+SEP+solrCmd, "delete", "-p", String.valueOf(solrPort), "-c", "pds",};
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");
			progProcess = Runtime.getRuntime().exec(execCmd);
			in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
			try{
                returnVal = progProcess.waitFor();
                if (returnVal!=0) {
                    print("Failed to delete search collection (pds).");
                    exit(1);
                }
                else 
                	print("Search collection (pds) is deleted successfully.");

            } catch(Exception ex){
               ex.printStackTrace();
            }
            in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void executeDockerBuild(String mode, String envStr) {
	//private static void executeDockerBuild(String mode) {
		print("Execute to " + mode + " Registry in Docker...");
		Process progProcess = null;
        int returnVal = -1;
		try {
			String[] execCmd = null;
			if (envStr==null) {
				if (prompt==null)
					execCmd = new String[] { dockerCmd, mode, registry_root};
				else
					execCmd = new String[] { dockerCmd, mode, prompt, registry_root};
			}
			else {
				if (prompt==null)
					execCmd = new String[] { envStr, dockerCmd, mode, registry_root};
				else
					execCmd = new String[] { envStr, dockerCmd, mode, prompt, registry_root};

			}
			//print("Executing '" + java.util.Arrays.toString(execCmd) + "'");

			progProcess = Runtime.getRuntime().exec(execCmd);
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(progProcess.getInputStream()));
			String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            try{
                returnVal = progProcess.waitFor();
                if (returnVal!=0) {
                    print("Failed to " + mode + " Registry in Docker.");
                    exit(1);
                }
                else  {
                	Thread.sleep(5000);
               		print("Completed to " + mode + " Registry in Docker.");
                }

            } catch(Exception ex){
               ex.printStackTrace();
            }
			in.close();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
            try {
                progProcess.getErrorStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                progProcess.getOutputStream().close();
            } catch (Exception e) {
            }
        }
	}

	private static void setPreset(String key, String value) {
		if (value != null) {
			RegistryInstallerUtils.getInstallerPresets().setProperty(key, value);
		}
	}
}
