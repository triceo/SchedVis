/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

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
import java.util.List;

import javax.swing.SwingWorker;

import cz.muni.fi.spc.SchedVis.model.SQL;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.parsers.machines.MachinesParser;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleParser;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventHasData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsJobRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsMachineRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEvent;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEventMove;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleJobData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleMachineData;

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
	AbstractMap<String, Integer> machineIds = new HashMap<String, Integer>();

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

	/**
	 * Create the database schema used by the application.
	 * 
	 * @throws SQLException
	 *             Thrown when any of the schema tables cannot be created.
	 */
	private void createSchema() throws SQLException {
		final Statement stmt = Importer.sql.getConnection().createStatement();
		// create machine groups' table
		try {
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machine_groups ("
					+ "id_machine_groups INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT UNIQUE);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating machine groups' table.", e);
		}
		// create machines table
		try {
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machines ("
					+ "id_machines INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT UNIQUE, " + "id_machine_groups INTEGER, "
					+ "cpus INTEGER, " + "speed INTEGER, " + "platform TEXT, "
					+ "os TEXT, " + "ram INTEGER, " + "hdd INTEGER);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating machines table.", e);
		}
		// create event types table
		try {
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS event_types ("
					+ "id_event_types INTEGER PRIMARY KEY, " + "name TEXT);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating event types' table.", e);
		}
		// create events' table
		try {
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS events ("
					+ "id_events INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "parent_id_events INTEGER, " + "id_event_types INTEGER, "
					+ "id_machines INTEGER, " + "id_machines_target INTEGER, "
					+ "id_jobs INTEGER, " + "clock INTEGER, "
					+ "need_cpus INTEGER, " + "need_platform TEXT, "
					+ "need_ram INTEGER, " + "need_hdd INTEGER, "
					+ "cpus_assigned TEXT, " + "expect_start INTEGER, "
					+ "expect_end INTEGER, " + "deadline INTEGER);");
		} catch (final SQLException e) {
			throw new SQLException("Error creating events' table.", e);
		}
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
		try {
			this.createSchema();
			Importer.sql.getConnection().setAutoCommit(false);
			this.parseMachines(new BufferedReader(new FileReader(
					this.machinesFile)));
			Importer.sql.getConnection().commit();
			this
					.parseDataSet(new BufferedReader(new FileReader(
							this.dataFile)));
			Importer.sql.getConnection().commit();
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
	 * @throws SQLException
	 *             Thrown when any of the rows failed to insert.
	 * @todo Implement transactions.
	 * @todo Implement foreign keys somehow. (No support in SQLite.)
	 * @todo Somehow make jobs a table of its own.
	 * @todo Somehow make assigned-CPUs a table if its own.
	 */
	private void parseDataSet(final BufferedReader reader) throws IOException,
			SQLException, cz.muni.fi.spc.SchedVis.parsers.schedule.ParseException {
		this.setProgress(0);
		final PreparedStatement eventTypeInsStmt = Importer.sql.getConnection()
				.prepareStatement(
						"INSERT INTO event_types (" + "id_event_types, "
								+ "name) VALUES (?, ?);");
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
		// prepare some statements
		final PreparedStatement eventStmt = Importer.sql.getConnection()
				.prepareStatement(
						"INSERT INTO events (" + "id_event_types, " + "clock, "
								+ "id_jobs, " + "id_machines, "
								+ "id_machines_target) VALUES (?, ?, ?, ?, ?)");
		final PreparedStatement eventDetailStmt = Importer.sql
				.getConnection()
				.prepareStatement(
						"INSERT INTO events ("
								+ "parent_id_events, "
								+ "id_machines, "
								+ "id_jobs, "
								+ "need_cpus, "
								+ "cpus_assigned, "
								+ "need_platform, "
								+ "need_ram, "
								+ "need_hdd, "
								+ "expect_start, "
								+ "expect_end, "
								+ "deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		// parse data set and fill the events' table
		new ScheduleParser(reader);
		final List<ScheduleEvent> events = ScheduleParser.read();
		final Integer totalEvents = events.size();
		Integer eventId = 0;
		for (final ScheduleEvent event : events) {
			eventId++;
			eventStmt.setInt(1, eventTypes.get(event.getName())); // evt. type
			eventStmt.setInt(2, event.getClock()); // clock
			if (event instanceof EventIsJobRelated) {
				eventStmt.setInt(3, ((EventIsJobRelated) event).getJob());
			}
			if (event instanceof EventIsMachineRelated) {
				eventStmt.setString(4, ((EventIsMachineRelated) event)
						.getMachine());
				if (event instanceof ScheduleEventMove) {
					eventStmt.setString(5, ((ScheduleEventMove) event)
							.getTargetMachine());
				}
			}
			eventStmt.execute();
			eventStmt.clearParameters();
			if (event instanceof EventHasData) {
				final List<ScheduleMachineData> data = ((EventHasData) event)
						.getData();
				final Integer parentEvent = eventId;
				for (final ScheduleMachineData machine : data) {
					eventDetailStmt.setInt(1, parentEvent);
					eventDetailStmt.setInt(2, Machine.getIdWithName(machine
							.getMachineId()));
					for (final ScheduleJobData job : machine.getJobs()) {
						eventDetailStmt.setInt(3, job.getNeededCPUs());
						eventDetailStmt.setString(4, new String(job
								.getAssignedCPUs()));
						eventDetailStmt.setString(5, job.getArch());
						eventDetailStmt.setInt(6, job.getNeededMemory());
						eventDetailStmt.setInt(7, job.getNeededSpace());
						eventDetailStmt.setInt(8, job.starts());
						eventDetailStmt.setInt(9, job.ends());
						eventDetailStmt.setInt(10, job.getDeadline());
						eventDetailStmt.execute();
					}
				}
			}
			this.setProgress((eventId / totalEvents) * 100);
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
	 * @throws SQLException
	 *             If any of the rows failed to insert.
	 * @todo Implement transactions.
	 * @todo Implement foreign keys somehow. (No support in SQLite.)
	 */
	private void parseMachines(final BufferedReader reader)
			throws cz.muni.fi.spc.SchedVis.parsers.machines.ParseException, SQLException {
		// prepare machine insertion query
		final PreparedStatement stmt = Importer.sql.getConnection()
				.prepareStatement(
						"INSERT INTO machines (" + "name, " + "cpus, "
								+ "speed, " + "platform, " + "os, " + "ram, "
								+ "hdd) VALUES (?, ?, ?, ?, ?, ?, ?);");
		new MachinesParser(reader);
		final List<cz.muni.fi.spc.SchedVis.parsers.machines.Machine> machines = MachinesParser
				.read();
		final Integer totalMachines = machines.size();
		Integer machineId = 0;
		for (final cz.muni.fi.spc.SchedVis.parsers.machines.Machine machine : machines) {
			machineId++;
			stmt.setString(1, machine.getName());
			stmt.setInt(2, machine.getCPUCount());
			stmt.setInt(3, machine.getSpeed());
			stmt.setString(4, machine.getArchitecture());
			stmt.setString(5, machine.getOperatingSystem());
			stmt.setInt(6, machine.getMemory());
			stmt.setInt(7, machine.getSpace());
			stmt.execute();
			this.setProgress((machineId / totalMachines) * 100);
		}
	}

}
