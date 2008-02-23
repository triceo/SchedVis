/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tool to import data from specific files into the SQLite database used by
 * this application.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Importer {

	private static SQL sql;
	private static Integer EVENT_JOB_ARRIVAL = 1;
	private static Integer EVENT_JOB_EXECUTION_START = 2;

	/**
	 * Parse the data set and insert its data into database.
	 * 
	 * @param reader
	 * @throws ParseException
	 * @throws IOException
	 * @throws SQLException
	 * @todo Implement transactions.
	 * @todo Implement foreign keys somehow. (No support in SQLite.)
	 * @todo Somehow make jobs a table of its own.
	 * @todo Somehow make assigned-CPUs a table if its own.
	 * @fixme Once data set format is sane, implement parsing of CPUs and
	 *        architectures.
	 */
	private static void parseDataSet(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
		Integer lineNo = 0;
		Integer charNo = 0;
		// create event types table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS event_types (id_event_types INTEGER PRIMARY KEY, name TEXT);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating event types' table.", e);
		}
		// fill it
		final PreparedStatement eventTypeInsStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO event_types (id_event_types, name) VALUES (?, ?);");
		final AbstractMap<String, Integer> eventTypes = new HashMap<String, Integer>();
		eventTypes.put("job-arrival", Importer.EVENT_JOB_ARRIVAL);
		eventTypes.put("job-execution-start",
				Importer.EVENT_JOB_EXECUTION_START);
		final Iterator<String> eventTypeIterator = eventTypes.keySet()
				.iterator();
		while (eventTypeIterator.hasNext()) {
			final String key = eventTypeIterator.next();
			eventTypeInsStmt.setInt(1, eventTypes.get(key));
			eventTypeInsStmt.setString(2, key);
			try {
				eventTypeInsStmt.execute();
			} catch (final SQLException e) {
				throw new SQLException("Error inserting event " + key + ".", e);
			}
		}
		// create events' table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS events (id_events INTEGER PRIMARY KEY AUTOINCREMENT, id_event_types INTEGER, id_machines TEXT, id_jobs INTEGER, clock FLOAT, need_cpus INTEGER, need_platform TEXT, need_ram INTEGER, need_hdd INTEGER, cpus_assigned TEXT, expect_start FLOAT, expect_end FLOAT, deadline FLOAT);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating events' table.", e);
		}
		// parse data set and fill the events' table
		boolean isEOF = false;
		while (!isEOF) {
			// prepare regexes
			final String floatRegex = "[\\d]+\\.[\\dE]+";
			final Pattern pattern = Pattern.compile("^([\\w\\-]*)\\s("
					+ floatRegex + ")\\s<([^|]+)(.*)>$");
			// read and parse
			final String line = reader.readLine();
			if (line == null) {
				isEOF = true;
			} else {
				lineNo++;
				final Matcher matcher = pattern.matcher(line);
				if (!matcher.matches()) {
					throw new ParseException(
							"Reached a malformatted dataset line: '" + line
									+ "'.", charNo);
				}
				// verify event type
				final String eventType = matcher.group(1);
				if (!eventTypes.containsKey(eventType)) {
					throw new ParseException("Invalid event type " + eventType
							+ " found.", charNo);
				}
				// get some more plain data
				final Double eventClock = new Double(matcher.group(2));
				final String eventMachine = matcher.group(3);
				// parse the job details
				final String unparsedData = matcher.group(4).trim();
				if (unparsedData.length() > 0) {
					final PreparedStatement eventInsStmt = Importer.sql
							.getConnection()
							.prepareStatement(
									"INSERT INTO events (id_event_types, id_machines, id_jobs, clock, need_cpus, need_platform, need_ram, need_hdd, cpus_assigned, expect_start, expect_end, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					// there may be multiple events
					final String preppedData = unparsedData.substring(1); // remove
																			// leading
																			// |
					String[] jobs;
					if (preppedData.contains("|")) {
						jobs = preppedData.split("\\|");
					} else {
						jobs = new String[1];
						jobs[0] = preppedData;
					}
					for (final String jobData : jobs) {
						if (jobData.length() == 0) {
							continue;
						}
						// general parameters
						eventInsStmt.setInt(1, eventTypes.get(eventType));
						eventInsStmt.setString(2, eventMachine);
						eventInsStmt.setDouble(4, eventClock);
						// specific parameters
						final String[] fields = jobData.split(";");
						if (fields.length != 7) {
							throw new ParseException(
									"Reached a malformatted dataset property line.",
									charNo);
						}
						eventInsStmt.setInt(3, new Integer(fields[0])); // job
																		// ID
						eventInsStmt.setInt(7, new Integer(fields[2])); // how
																		// much
																		// ram
																		// needed
						eventInsStmt.setInt(8, new Integer(fields[3])); // how
																		// much
																		// hdd
																		// needed
						eventInsStmt.setDouble(9, new Double(fields[4])); // expected
																			// start
						eventInsStmt.setDouble(10, new Double(fields[5])); // expected
																			// finish
						final Double deadline = new Double(fields[6]);
						if (deadline >= 0) { // negative values mean no
												// deadline
							eventInsStmt.setDouble(11, new Double(fields[6]));
						}
						eventInsStmt.execute();
					}
				} else {
					final PreparedStatement eventInsStmt = Importer.sql
							.getConnection()
							.prepareStatement(
									"INSERT INTO events (id_event_types, id_machines, clock) VALUES (?, ?, ?);");
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
	 * @todo Implement transactions.
	 * @todo Implement foreign keys somehow. (No support in SQLite.)
	 */
	private static void parseMachines(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
		boolean isEOF = false;
		Integer lineNo = 0;
		Integer charNo = 0;
		// create machines table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS machines (id_machines TEXT PRIMARY KEY, speed INTEGER, platform TEXT, os TEXT, ram INTEGER, hdd INTEGER);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating machines table.", e);
		}
		// create CPUs table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS machine_cpus (id_machines TEXT, id_cpus INTEGER, PRIMARY KEY (id_machines, id_cpus));");
		} catch (final SQLException e) {
			throw new SQLException("Error creating table for machine CPUs.", e);
		}
		// prepare machine insertion query
		final PreparedStatement machineInsStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO machines (id_machines, speed, platform, os, ram, hdd) VALUES (?, ?, ?, ?, ?, ?);");
		// prepare machine CPU insertion query
		final PreparedStatement machineCpuInsStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO machine_cpus (id_machines, id_cpus) VALUES (?, ?);");
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
				final String machineId = fields[0];
				machineInsStmt.setString(1, machineId); // machine name
				machineInsStmt.setInt(2, new Integer(fields[2])); // machine
																	// speed
				machineInsStmt.setString(3, fields[3]); // machine platform
				machineInsStmt.setString(4, fields[4]); // machine os
				machineInsStmt.setInt(5, new Integer(fields[5])); // machine
																	// RAM
				machineInsStmt.setInt(6, new Integer(fields[6])); // machine
																	// HDD
				// insert machine CPUs
				try {
					machineInsStmt.execute();
				} catch (final SQLException e) {
					throw new SQLException(
							"Error inserting a machine. Probably a duplicate machine name.",
							e);
				}
				for (Integer cpuId = 1; cpuId <= new Integer(fields[1]); cpuId++) {
					machineCpuInsStmt.setString(1, machineId);
					machineCpuInsStmt.setInt(2, cpuId);
					try {
						machineCpuInsStmt.execute();
					} catch (final SQLException e) {
						throw new SQLException("Error inserting machine #"
								+ machineId + "'s CPU #" + cpuId
								+ ". Probably already exists.", e);
					}
				}
				charNo += line.length();
			}
		}
	}

	public static boolean process(final File machinesFile, final File dataFile,
			final String name) {
		try {
			Importer.sql = SQL.getInstance(name, true);
		} catch (final Exception e) {
			return false;
		}
		if (!machinesFile.canRead() || !dataFile.canRead()) {
			return false;
		}
		try {
			Importer.parseMachines(new BufferedReader(new FileReader(
					machinesFile)));
			Importer.parseDataSet(new BufferedReader(new FileReader(dataFile)));
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

}
