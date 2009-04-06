/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The class that is used to access every bit of SchedVis configuration. It is
 * a singleton.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Configuration {

	private static Configuration instance;

	/**
	 * Retrieve the file that holds the SQLite database.
	 * 
	 * @return A database file.
	 */
	public static File getDatabaseFile() {
		return new File(Configuration.getProperties().getProperty("files.database",
		    "Production.sqlite")).getAbsoluteFile();
	}

	/**
	 * Retrieve the file that holds the available events.
	 * 
	 * @return Events file.
	 */
	public static File getEventsFile() {
		return new File(Configuration.getProperties().getProperty("files.events",
		    "Data-set.txt")).getAbsoluteFile();
	}

	/**
	 * Retrieve the single instance of this class.
	 * 
	 * @return The single Configuration instance.
	 * @throws IOException
	 *           Thrown when the configuration file cannot be read.
	 */
	private static Configuration getInstance() throws IOException {
		if (Configuration.instance == null) {
			Configuration.instance = new Configuration();
		}
		return Configuration.instance;
	}

	/**
	 * Retrieve the file that holds the available machines.
	 * 
	 * @return Machines file.
	 */
	public static File getMachinesFile() {
		return new File(Configuration.getProperties().getProperty("files.machines",
		    "machines.txt")).getAbsoluteFile();
	}

	public static Integer getMaxImageWidth() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.max_image_width", "800"));
	}

	/**
	 * Retrieve the number of CPU cores that the application has available.
	 * 
	 * @return Number of cores, never lower than 1.
	 */
	public static Integer getNumberOfCPUCores() {
		return Math.max(Integer.valueOf(Configuration.getProperties().getProperty(
		    "system.num_cores", "1")), Runtime.getRuntime().availableProcessors());
	}

	public static Integer getNumberOfPixelsPerCPU() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.pixels_per_cpu"));
	}

	public static Integer getNumberOfTicksPerGuide() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.ticks_per_guide", "5"));
	}

	/**
	 * Retrieve the parsed configuration file.
	 * 
	 * @return The object holding all the configuration values on success, empty
	 *         object on failure.
	 */
	protected static Properties getProperties() {
		try {
			return Configuration.getInstance().p;
		} catch (IOException e) {
			return new Properties();
		}
	}

	/**
	 * Retrieve the temporary folder in which the application might store its
	 * files.
	 * 
	 * @return The temporary folder.
	 */
	public static File getTempFolder() {
		return new File(Configuration.getProperties().getProperty("folders.temp",
		    ".tmp")).getAbsoluteFile();
	}

	private final Properties p = new Properties();

	/**
	 * Class constructor.
	 * 
	 * @throws IOException
	 *           When the configuration cannot be read.
	 */
	private Configuration() throws IOException {
		try {
			FileInputStream in = new FileInputStream("config.properties");
			this.p.load(in);
		} catch (Exception e) {
			throw new IOException("Problem reading config file.", e);
		}
	}
}
