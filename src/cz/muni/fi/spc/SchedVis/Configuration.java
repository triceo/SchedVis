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
		    "system.num_cores", "1")), Runtime.getRuntime().availableProcessors());
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
