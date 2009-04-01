/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author lpetrovi
 * 
 */
public class Configuration {

	private static Configuration instance;

	public static File getDatabaseFile() {
		return new File(Configuration.getProperties().getProperty("files.database",
		    "Production.sqlite")).getAbsoluteFile();
	}

	public static File getEventsFile() {
		return new File(Configuration.getProperties().getProperty("files.events",
		    "Data-set.txt")).getAbsoluteFile();
	}

	private static Configuration getInstance() throws IOException {
		if (Configuration.instance == null) {
			Configuration.instance = new Configuration();
		}
		return Configuration.instance;
	}

	public static File getMachinesFile() {
		return new File(Configuration.getProperties().getProperty("files.machines",
		    "machines.txt")).getAbsoluteFile();
	}

	public static Integer getNumberOfCPUCores() {
		return Math.max(Integer.valueOf(Configuration.getProperties().getProperty(
		    "system.num_cores", "1")), 1);
	}

	protected static Properties getProperties() {
		try {
			return Configuration.getInstance().p;
		} catch (IOException e) {
			return new Properties();
		}
	}

	public static File getTempFolder() {
		return new File(Configuration.getProperties().getProperty("folders.temp",
		    System.getProperty("java.io.tempdir"))).getAbsoluteFile();
	}

	private final Properties p = new Properties();

	private Configuration() throws IOException {
		try {
			FileInputStream in = new FileInputStream("config.properties");
			this.p.load(in);
		} catch (Exception e) {
			throw new IOException("Problem reading config file.", e);
		}
	}
}
