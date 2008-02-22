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
			this.parseMachines(new BufferedReader(new FileReader(mFile)));
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

	private void parseDataSet(final BufferedReader reader)
			throws ParseException, IOException {
	}

	/**
	 * Parse the machines' input file, each line of which looks like this:
	 * "${NAME};${CPUs};${SPEED};${PLATFORM};${OS};${RAM};${HDD}" Where: ${NAME}
	 * is the name of the machine, ${CPUs} is the number of processors inside,
	 * ${SPEED} is the total cumulative speed of those CPUs, ${PLATFORM} is the
	 * architecture that the machine uses, ${OS} is the operating system the
	 * machine runs, ${RAM} is the amount of RAM available in the machine (in MBs)
	 * and ${HDD} is the amount of hard drive space available (in MBs)
	 * 
	 * @param reader
	 * @throws ParseException
	 * @throws IOException
	 * TODO Implement transaction.
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
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machines (id_machines INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, speed INTEGER, platform TEXT, os TEXT, ram INTEGER, hdd INTEGER);");
		} catch (SQLException e) {
			throw new SQLException("Error creating machines table.", e);
		}
		// create CPUs table
		try {
			final Statement stmt = SQL.conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS machine_cpus (id_machines INTEGER, id_cpus INTEGER, PRIMARY KEY (id_machines, id_cpus));");
		} catch (SQLException e) {
			throw new SQLException("Error creating table for machine CPUs.", e);
		}
		// prepare machine insertion query
		PreparedStatement machineInsStmt = SQL.conn.prepareStatement("INSERT INTO machines (name, speed, platform, os, ram, hdd) VALUES (?, ?, ?, ?, ?, ?);");
		// prepare machine CPU insertion query
		PreparedStatement machineCpuInsStmt = SQL.conn.prepareStatement("INSERT INTO machine_cpus (id_machines, id_cpus) VALUES (?, ?);");
		Integer machineId = 0;
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
				machineInsStmt.setString(1, fields[0]); // machine name
				machineInsStmt.setInt(2, new Integer(fields[2])); // machine speed
				machineInsStmt.setString(3, fields[3]); //machine platform
				machineInsStmt.setString(4, fields[4]); // machine os
				machineInsStmt.setInt(5, new Integer(fields[5])); // machine RAM
				machineInsStmt.setInt(6, new Integer(fields[6])); // machine HDD
				// insert machine CPUs
				try {
					machineInsStmt.execute();
					machineId++;
				} catch (SQLException e) {
					throw new SQLException("Error inserting a machine. Probably a duplicate machine name.", e);
				}
				for (Integer cpuId = 1; cpuId <= new Integer(fields[1]); cpuId++) {
					machineCpuInsStmt.setInt(1, machineId);
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
