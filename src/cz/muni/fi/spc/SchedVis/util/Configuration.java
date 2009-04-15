/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The class that is used to access every bit of SchedVis configuration. It is a
 * singleton.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Configuration {

	private static Properties p;

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

	/**
	 * Number of pixels each CPU should take up on the y axis.
	 * 
	 * @return Will always be even and >= 5.
	 */
	public static Integer getNumberOfPixelsPerCPU() {
		Integer minValue = 5;
		Integer actualValue = Integer.valueOf(Configuration.getProperties()
		    .getProperty("graphics.pixels_per_cpu", minValue.toString()));
		if (actualValue % 2 == 0) {
			actualValue++;
		}
		return Math.max(minValue, actualValue);
	}

	public static Integer getNumberOfSchedulesLookaback() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "lookahead.previous", "1"));
	}

	public static Integer getNumberOfSchedulesLookahead() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "lookahead.next", "1"));
	}

	public static Integer getNumberOfTicksPerGuide() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.ticks_per_guide", "5"));
	}

	public static Integer getPlayDelay() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "play.delay", "1000"));
	}

	/**
	 * Retrieve the parsed configuration file.
	 * 
	 * @return The object holding all the configuration values on success, empty
	 *         object on failure.
	 */
	protected static Properties getProperties() {
		if (Configuration.p == null) {
			try {
				FileInputStream in = new FileInputStream("config.properties");
				Configuration.p = new Properties();
				Configuration.p.load(in);
			} catch (Exception e) {
				Configuration.p = new Properties();
				Logger.getLogger(Configuration.class).error(
				    "Failed to load configuration file, caught: " + e + ".");
			}
		}
		return Configuration.p;
	}

	/**
	 * Class constructor.
	 * 
	 * @throws IOException
	 *           When the configuration cannot be read.
	 */
	private Configuration() throws IOException {
	}
}
