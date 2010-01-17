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
import java.util.Properties;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * The class that is used to access every bit of SchedVis configuration. It is a
 * singleton.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Configuration {

	private static Properties p;

	public static boolean createGroupPerMachine() {
		final String val = Configuration.getProperties().getProperty(
		    "import.group_per_machine", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		if (val.equals("1")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public static Integer getBenchmarkFrequency() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "benchmark.every_nth_event", "100")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static Integer getBenchmarkIterations() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "benchmark.number_of_runs", "5")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Retrieve the file that holds the SQLite database.
	 * 
	 * @return A database file.
	 */
	public static File getDatabaseFile() {
		return new File(Configuration.getProperties().getProperty("files.database", //$NON-NLS-1$
		    "Production.sqlite")).getAbsoluteFile(); //$NON-NLS-1$
	}

	/**
	 * Retrieve the file that holds the available events.
	 * 
	 * @return Events file.
	 */
	public static File getEventsFile() {
		return new File(Configuration.getProperties().getProperty("files.events", //$NON-NLS-1$
		    "Data-set.txt")).getAbsoluteFile(); //$NON-NLS-1$
	}

	/**
	 * Retrieve the file that holds the available machines.
	 * 
	 * @return Machines file.
	 */
	public static File getMachinesFile() {
		return new File(Configuration.getProperties().getProperty("files.machines", //$NON-NLS-1$
		    "machines.txt")).getAbsoluteFile(); //$NON-NLS-1$
	}

	/**
	 * The maximum allowed width of the schedule image.
	 * 
	 * @return Image width in pixels.
	 */
	public static Integer getMaxImageWidth() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.max_image_width", "800")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Number of pixels each CPU should take up on the y axis.
	 * 
	 * @return Will always be even and >= 5.
	 */
	public static Integer getNumberOfPixelsPerCPU() {
		final Integer minValue = 5;
		Integer actualValue = Integer.valueOf(Configuration.getProperties()
		    .getProperty("graphics.pixels_per_cpu", minValue.toString())); //$NON-NLS-1$
		if (actualValue % 2 == 0) {
			actualValue++;
		}
		return Math.max(minValue, actualValue);
	}

	/**
	 * The length between guiding lines on the x axis of a schedule.
	 * 
	 * @return Number of ticks between every two guiding lines.
	 */
	public static Integer getNumberOfTicksPerGuide() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "graphics.ticks_per_guide", "5")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Get the delay that the "play" function uses between frames.
	 * 
	 * @return A number of milliseconds to wait before showing another frame.
	 */
	public static Integer getPlayDelay() {
		return Integer.valueOf(Configuration.getProperties().getProperty(
		    "play.delay", "1000")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Retrieve the parsed configuration file.
	 * 
	 * @return The object holding all the configuration values on success, empty
	 *         object on failure.
	 */
	protected synchronized static Properties getProperties() {
		if (Configuration.p == null) {
			try {
				final FileInputStream in = new FileInputStream("bin/config.properties"); //$NON-NLS-1$
				Configuration.p = new Properties();
				Configuration.p.load(in);
			} catch (final Exception e) {
				Configuration.p = new Properties();
				Logger.getLogger(Configuration.class).error(
				    new PrintfFormat(Messages.getString("Configuration.17")) //$NON-NLS-1$
				        .sprintf(e.getLocalizedMessage()));
			}
		}
		return Configuration.p;
	}

}
