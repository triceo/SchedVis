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

import javax.swing.SwingWorker;

/**
 * A tool to import data from specific files into the SQLite database used by
 * this application.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Importer extends SwingWorker<Void, Void> {

	private static SQL sql;
	private static Integer EVENT_JOB_ARRIVAL = 1;
	private static Integer EVENT_JOB_EXECUTION_START = 2;
	private static Integer EVENT_JOB_CANCEL = 3;
	private static Integer EVENT_MACHINE_RESTART = 4;
	private static Integer EVENT_MACHINE_FAILURE = 5;
	private static Integer EVENT_JOB_MOVE_GOOD = 6;
	private static Integer EVENT_JOB_MOVE_BAD = 7;
	private static Integer EVENT_MACHINE_RESTART_JOB_MOVE_GOOD = 8;
	private static Integer EVENT_MACHINE_RESTART_JOB_MOVE_BAD = 9;
	private static Integer EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD = 10;
	private static Integer EVENT_MACHINE_FAILURE_JOB_MOVE_BAD = 11;

	private final File machinesFile;
	private final File dataFile;
	private final String name;

	private boolean result = false;

	public Importer(final File machinesFile, final File dataFile,
			final String name) {
		this.machinesFile = machinesFile;
		this.dataFile = dataFile;
		this.name = name;
	}

	@Override
	public Void doInBackground() {
		try {
			Importer.sql = SQL.getInstance(this.name, true);
		} catch (final Exception e) {
			return null;
		}
		if (!this.machinesFile.canRead() || !this.dataFile.canRead()) {
			return null;
		}
		this.updateProgressBar(true, 0);
		try {
			this.parseMachines(new BufferedReader(new FileReader(
					this.machinesFile)));
			this
					.parseDataSet(new BufferedReader(new FileReader(
							this.dataFile)));
		} catch (final Exception e) {
			return null;
		}
		this.result = true;
		return null;
	}

	public boolean isSuccess() {
		return this.result;
	}

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
	private void parseDataSet(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
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
		eventTypes.put("job-cancel", Importer.EVENT_JOB_CANCEL);
		eventTypes.put("good-move", Importer.EVENT_JOB_MOVE_GOOD);
		eventTypes.put("bad-move", Importer.EVENT_JOB_MOVE_BAD);
		eventTypes.put("machine-failure", Importer.EVENT_MACHINE_FAILURE);
		eventTypes.put("machine-failure-move-good",
				Importer.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD);
		eventTypes.put("machine-failure-move-bad",
				Importer.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD);
		eventTypes.put("machine-restart", Importer.EVENT_MACHINE_RESTART);
		eventTypes.put("machine-restart-move-good",
				Importer.EVENT_MACHINE_RESTART_JOB_MOVE_GOOD);
		eventTypes.put("machine-restart-move-bad",
				Importer.EVENT_MACHINE_RESTART_JOB_MOVE_BAD);
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
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS events ("
					+ "id_events INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "parent_id_events INTEGER, " + "id_event_types INTEGER, "
					+ "id_machines TEXT, " + "id_machines_target TEXT, "
					+ "id_jobs INTEGER, " + "clock INTEGER, "
					+ "need_cpus INTEGER, " + "need_platform TEXT, "
					+ "need_ram INTEGER, " + "need_hdd INTEGER, "
					+ "cpus_assigned TEXT, " + "expect_start INTEGER, "
					+ "expect_end INTEGER, " + "deadline INTEGER);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating events' table.", e);
		}
		// prepare some statements
		final PreparedStatement eventStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO events (id_event_types, clock, id_jobs, id_machines, id_machines_target) VALUES (?, ?, ?, ?, ?)");
		final PreparedStatement eventDetailStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO events (parent_id_events, id_machines, id_jobs, need_cpus, cpus_assigned, need_platform, need_ram, need_hdd, expect_start, expect_end, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		// parse data set and fill the events' table
		boolean isEOF = false;
		Integer charCount = 0;
		Integer eventId = 0;
		while (!isEOF) {
			// read and parse
			final String line = reader.readLine();
			if (line == null) {
				isEOF = true;
			} else {
				final String[] parts = line.split("\t");
				if (!eventTypes.containsKey(parts[0])) { // verify event
					throw new ParseException("Unknown event received: "
							+ parts[0], charCount);
				}
				final Integer clock = new Integer(parts[1]);
				String affectedMachineId = "";
				String targetMachineId = "";
				Integer jobId = null;
				String[] parseData = null;
				switch (parts.length) { // verify event-specific parameters
				case 3: // simple machine-(failure|restart) events
					affectedMachineId = parts[2];
					break;
				case 7: // (.*)move-job events
					jobId = new Integer(parts[2]);
					affectedMachineId = parts[3];
					targetMachineId = parts[4];
					parseData = new String[2];
					parseData[0] = parts[5];
					parseData[1] = parts[6];
					break;
				case 4: // other known events
					if (eventTypes.get(parts[0]).equals(
							Importer.EVENT_JOB_EXECUTION_START)) {
						final String strippedLine = parts[3].substring(1,
								parts[3].length() - 1);
						final String[] strParts = strippedLine.split(";");
						affectedMachineId = strParts[0];
					}
					parseData = new String[1];
					parseData[0] = parts[3];
					jobId = new Integer(parts[2]);
					break;
				default:
					throw new ParseException(
							"Unknown parameter count for an event: "
									+ parts.length, charCount);
				}
				eventStmt.clearParameters();
				eventStmt.setInt(1, eventTypes.get(parts[0]));
				eventStmt.setInt(2, clock);
				if (parseData != null) {
					eventStmt.setInt(3, jobId);
				}
				if (affectedMachineId.length() > 0) {
					eventStmt.setString(4, affectedMachineId);
				}
				if (targetMachineId.length() > 0) {
					eventStmt.setString(5, targetMachineId);
				}
				eventStmt.execute();
				eventId++;
				final Integer parentEventId = eventId;
				if (parseData != null) { // parse the parameters
					for (String machineLine : parseData) {
						if (!machineLine.startsWith("<")
								|| !machineLine.endsWith(">")) {
							throw new ParseException(
									"Bad line with parameters.", charCount);
						}
						machineLine = machineLine.substring(1, machineLine
								.length() - 1);
						final String[] params = machineLine.split("\\|");
						if (params.length == 1) {
							// params contain only ID of a machine; do nothing
						} else if (params.length < 1) {
							throw new ParseException(
									"Bad line with parameters: " + machineLine,
									charCount);
						} else {
							for (Integer job = 1; job < params.length; job++) {
								final String jobDetails[] = params[job]
										.split(";");
								eventDetailStmt.clearParameters();
								eventDetailStmt.setInt(1, parentEventId);
								eventDetailStmt.setString(2, params[0]);
								eventDetailStmt.setInt(3, new Integer(
										jobDetails[0]));
								eventDetailStmt.setInt(4, new Integer(
										jobDetails[1]));
								eventDetailStmt.setString(5, jobDetails[2]);
								eventDetailStmt.setString(6, jobDetails[3]);
								eventDetailStmt.setInt(7, new Integer(
										jobDetails[4]));
								eventDetailStmt.setInt(8, new Integer(
										jobDetails[5]));
								eventDetailStmt.setInt(9, new Integer(
										jobDetails[6]));
								eventDetailStmt.setInt(10, new Integer(
										jobDetails[7]));
								if (!jobDetails[8].equals("-1")) { // deadline
									eventDetailStmt.setInt(11, new Integer(
											jobDetails[8]));
								}
								eventDetailStmt.execute();
								eventId++;
							}
						}
					}
				}
				charCount += line.length(); // take the line as parsed
				this.updateProgressBar(false, charCount);
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
	private void parseMachines(final BufferedReader reader)
			throws ParseException, IOException, SQLException {
		boolean isEOF = false;
		Integer lineNo = 0;
		Integer charNo = 0;
		// create machine groups' table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS machine_groups (id_machine_groups INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating machines table.", e);
		}
		// create machines table
		try {
			final Statement stmt = Importer.sql.getConnection()
					.createStatement();
			stmt
					.executeUpdate("CREATE TABLE IF NOT EXISTS machines (id_machines TEXT PRIMARY KEY, id_machine_groups INTEGER, speed INTEGER, platform TEXT, os TEXT, ram INTEGER, hdd INTEGER);");
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
				this.updateProgressBar(true, charNo);
			}
		}
	}

	private void updateProgressBar(boolean parsingMachines,
			final Integer charCount) {
		final Double totalLength = new Double(this.machinesFile.length()
				+ this.dataFile.length());
		Double readLength = new Double(charCount);
		if (!parsingMachines) {
			readLength += this.machinesFile.length();
		}
		this.setProgress(new Double((readLength * 100) / totalLength)
				.intValue());
	}

}
