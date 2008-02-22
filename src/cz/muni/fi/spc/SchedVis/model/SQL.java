/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import SQLite.Database;

/**
 * Base class for SchedVis' data model. Uses SQLite as its backend. It is a
 * singleton as we'll never need more than 1 model.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class SQL {

	private static SQL instance = null;
	private static Connection conn = null;
	private static Database db = null;
	private static Integer EVENT_JOB_ARRIVAL = 1;
	private static Integer EVENT_JOB_EXECUTION_START = 2;

	/**
	 * Get instance of this class.
	 * 
	 * @param name
	 *            Name of the model to load.
	 * @return The model.
	 * @throws java.lang.Exception
	 */
	public static SQL getInstance(final String name) throws java.lang.Exception {
		if (SQL.instance == null) {
			SQL.instance = new SQL(name);
		}
		return SQL.instance;
	}

	/**
	 * Singleton constructor.
	 * 
	 * @param name
	 *            Name of the database to load model from.
	 * @throws java.lang.Exception
	 */
	// TODO find more specific exception(s) to throw
	protected SQL(final String name) throws Exception {
		final String dbName = this.getDatabaseName(name);
		final File dbFile = new File(dbName);
		// FIXME change this once we are not debugging model.
		final boolean fileExists = dbFile.exists();
		if (fileExists) dbFile.delete();
		// initialize SQLite
		try {
			Class.forName("SQLite.JDBCDriver").newInstance();
			SQL.conn = DriverManager.getConnection("jdbc:sqlite:/" + dbName);
			final java.lang.reflect.Method m = SQL.conn.getClass().getMethod(
					"getSQLiteDatabase", (Class<?>[]) null);
			SQL.db = (SQLite.Database) m.invoke(SQL.conn, (Object[]) null);
		} catch (final Exception e) {
			throw new Exception("Failed instantiating the model.", e);
		}
		try {
			this.createDb();
		} catch (final FileNotFoundException e) {
			throw new java.lang.Exception(
					"Failed loading input files, cannot create model.", e);
		}
	}

	private void createDb() throws Exception {
		final File mFile = new File("machines.txt");
		final File dsFile = new File("Data-set.txt");
		if (!dsFile.exists()) {
			throw new FileNotFoundException(
					"'Data-set.txt' input file not found.");
		}
		if (!mFile.exists()) {
			throw new FileNotFoundException(
					"'machines.txt' input file not found.");
		}
		try {
			//this.parseMachines(new BufferedReader(new FileReader(mFile)));
			this.parseDataSet(new BufferedReader(new FileReader(dsFile)));
		} catch (final ParseException e) {
			throw new ParseException("Error while parsing input files.", e
					.getErrorOffset());
		} catch (final IOException e) {
			throw new IOException("Error while reading input files.", e);
		} catch (final SQLException e) {
			throw new SQLException(
					"Error while communicating with the database.", e);
		}
	}

	private String getDatabaseName(final String name) {
		return name + ".sqlite";
	}

	/**
	 * Parse the data set and insert its data into database.
	 * 
	 * @param reader
	 * @throws ParseException
	 * @throws IOException
	 * @throws SQLException
	 * TODO Implement transactions.
	 * TODO Implement foreign keys somehow. (No support in SQLite.)
	 * TODO Somehow make jobs a table of its own.
	 * TODO Somehow make assigned-CPUs a table if its own.
	 * FIXME Once data set format is sane, implement parsing of CPUs and architectures.
	 */
	private void parseDataSet(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
		Integer lineNo = 0;
		Integer charNo = 0;
		// create event types table
		try {
			final Statement stmt = SQL.conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS event_types (id_event_types INTEGER PRIMARY KEY, name TEXT);");
		} catch (SQLException e) {
			throw new SQLException("Error creating event types' table.", e);
		}
		// fill it
		PreparedStatement eventTypeInsStmt = SQL.conn.prepareStatement("INSERT INTO event_types (id_event_types, name) VALUES (?, ?);");
		AbstractMap<String, Integer> eventTypes = new HashMap<String, Integer>();
		eventTypes.put("job-arrival", EVENT_JOB_ARRIVAL);
		eventTypes.put("job-execution-start", EVENT_JOB_EXECUTION_START);
		Iterator<String> eventTypeIterator = eventTypes.keySet().iterator();
		while (eventTypeIterator.hasNext()) {
			String key = eventTypeIterator.next();
			eventTypeInsStmt.setInt(1, eventTypes.get(key));
			eventTypeInsStmt.setString(2, key);
			try {
				eventTypeInsStmt.execute();
			} catch (SQLException e) {
				throw new SQLException("Error inserting event " + key + ".", e);
			}
		}
		// create events' table
		try {
			final Statement stmt = SQL.conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS events (id_events INTEGER PRIMARY KEY AUTOINCREMENT, id_event_types INTEGER, id_machines TEXT, id_jobs INTEGER, clock FLOAT, need_cpus INTEGER, need_platform TEXT, need_ram INTEGER, need_hdd INTEGER, cpus_assigned TEXT, expect_start FLOAT, expect_end FLOAT, deadline FLOAT);");
		} catch (SQLException e) {
			throw new SQLException("Error creating events' table.", e);
		}
		// parse data set and fill the events' table
		boolean isEOF = false;
		while (!isEOF) {
			// prepare regexes
			String floatRegex = "[\\d]+\\.[\\dE]+";
			//Pattern pattern = Pattern.compile("(\\w[\\w\\-]+) (" + floatRegex +") <([\\w:]+)(|(.*))*>");
			Pattern pattern = Pattern.compile("^([\\w\\-]*)\\s(" + floatRegex + ")\\s<([^|]+)(.*)>$");
			// read and parse
			final String line = reader.readLine();
			if (line == null) {
				isEOF = true;
			} else {
				lineNo++;
	            Matcher matcher = pattern.matcher(line);
				if (!matcher.matches()) {
					throw new ParseException("Reached a malformatted dataset line.", charNo);
				}
				// verify event type
				String eventType = matcher.group(1);
				if (!eventTypes.containsKey(eventType)) {
					throw new ParseException("Invalid event type " + eventType + " found.", charNo);
				}
				// get some more plain data
				Double eventClock = new Double(matcher.group(2));
				String eventMachine = matcher.group(3);
				// parse the job details
				String unparsedData = matcher.group(4).trim();
				if (unparsedData.length() > 0) {
					PreparedStatement eventInsStmt = SQL.conn.prepareStatement("INSERT INTO events (id_event_types, id_machines, id_jobs, clock, need_cpus, need_platform, need_ram, need_hdd, cpus_assigned, expect_start, expect_end, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					// there may be multiple events
					String preppedData = unparsedData.substring(1); // remove leading |
					String[] jobs;
					if (preppedData.contains("|")) {
						jobs = preppedData.split("\\|");
					} else {
						jobs = new String[1];
						jobs[0] = preppedData;
					}
					for (String jobData: jobs) {
						if (jobData.length() == 0) {
							continue;
						}
						// general parameters
						eventInsStmt.setInt(1, eventTypes.get(eventType));
						eventInsStmt.setString(2, eventMachine);
						eventInsStmt.setDouble(4, eventClock);
						// specific parameters
						String[] fields = jobData.split(";");
						if (fields.length != 7) {
							throw new ParseException("Reached a malformatted dataset property line.", charNo);
						}
						eventInsStmt.setInt(3, new Integer(fields[0])); // job ID
						eventInsStmt.setInt(7, new Integer(fields[2])); // how much ram needed
						eventInsStmt.setInt(8, new Integer(fields[3])); // how much hdd needed
						eventInsStmt.setDouble(9, new Double(fields[4])); // expected start
						eventInsStmt.setDouble(10, new Double(fields[5])); // expected finish
						Double deadline = new Double(fields[6]);
						if (deadline >= 0) { // negative values mean no deadline
							eventInsStmt.setDouble(11, new Double(fields[6]));
						}
						eventInsStmt.execute();
					}
				} else {
					PreparedStatement eventInsStmt = SQL.conn.prepareStatement("INSERT INTO events (id_event_types, id_machines, clock) VALUES (?, ?, ?);");
					eventInsStmt.setInt(1, eventTypes.get(eventType));
					eventInsStmt.setString(2, eventMachine);
					eventInsStmt.setDouble(3, eventClock);
					eventInsStmt.execute();
				}
				charNo += line.length();
			}
		}
	}

	/**
	 * Parse the machines' input file, each line of which looks like this:
	 * "${NAME};${CPUs};${SPEED};${PLATFORM};${OS};${RAM};${HDD}" Where: ${NAME}
	 * is the name of the machine, ${CPUs} is the number of processors inside,
	 * ${SPEED} is the total cumulative speed of those CPUs (in MIPS), 
	 * ${PLATFORM} is the architecture that the machine uses, ${OS} is the 
	 * operating system the machine runs, ${RAM} is the amount of RAM available 
	 * in the machine (in MBs) and ${HDD} is the amount of hard drive space 
	 * available (in MBs)
	 * 
	 * @param reader
	 * @throws ParseException
	 * @throws IOException
	 * TODO Implement transactions.
	 * TODO Implement foreign keys somehow. (No support in SQLite.)
	 */
	private void parseMachines(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
		boolean isEOF = false;
		Integer lineNo = 0;
		Integer charNo = 0;
		// create machines table
		try {
			final Statement stmt = SQL.conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machines (id_machines TEXT PRIMARY KEY, speed INTEGER, platform TEXT, os TEXT, ram INTEGER, hdd INTEGER);");
		} catch (SQLException e) {
			throw new SQLException("Error creating machines table.", e);
		}
		// create CPUs table
		try {
			final Statement stmt = SQL.conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machine_cpus (id_machines TEXT, id_cpus INTEGER, PRIMARY KEY (id_machines, id_cpus));");
		} catch (SQLException e) {
			throw new SQLException("Error creating table for machine CPUs.", e);
		}
		// prepare machine insertion query
		PreparedStatement machineInsStmt = SQL.conn.prepareStatement("INSERT INTO machines (id_machines, speed, platform, os, ram, hdd) VALUES (?, ?, ?, ?, ?, ?);");
		// prepare machine CPU insertion query
		PreparedStatement machineCpuInsStmt = SQL.conn.prepareStatement("INSERT INTO machine_cpus (id_machines, id_cpus) VALUES (?, ?);");
		while (!isEOF) {
			lineNo++;
			final String line = reader.readLine();
			if (line == null) {
				isEOF = true;
			} else {
				final String[] fields = line.split(";");
				if (fields.length != 7) {
					throw new ParseException(
							"Reached a malformatted machine line.", charNo);
				}
				// insert machine line into SQL
				String machineId = fields[0];
				machineInsStmt.setString(1, machineId); // machine name
				machineInsStmt.setInt(2, new Integer(fields[2])); // machine speed
				machineInsStmt.setString(3, fields[3]); //machine platform
				machineInsStmt.setString(4, fields[4]); // machine os
				machineInsStmt.setInt(5, new Integer(fields[5])); // machine RAM
				machineInsStmt.setInt(6, new Integer(fields[6])); // machine HDD
				// insert machine CPUs
				try {
					machineInsStmt.execute();
				} catch (SQLException e) {
					throw new SQLException("Error inserting a machine. Probably a duplicate machine name.", e);
				}
				for (Integer cpuId = 1; cpuId <= new Integer(fields[1]); cpuId++) {
					machineCpuInsStmt.setString(1, machineId);
					machineCpuInsStmt.setInt(2, cpuId);
					try {
						machineCpuInsStmt.execute();
					} catch (SQLException e) {
						throw new SQLException("Error inserting machine #" + machineId + "'s CPU #" + cpuId + ". Probably already exists.", e);
					}
				}
				charNo += line.length();
			}
		}
	}
	
	public void finalize() throws Throwable {
		conn.close();
		db.close();
		super.finalize();
	}

}
