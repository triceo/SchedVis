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
import java.text.ParseException;

import SQLite.Database;

/**
 * Base class for SchedVis' data model. Uses SQLite as its backend. It is a
 * singleton as we'll never need more than 1 model.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class Model {

	private static Model instance = null;
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
	public static Model getInstance(final String name)
			throws java.lang.Exception {
		if (Model.instance == null) {
			Model.instance = new Model(name);
		}
		return Model.instance;
	}

	/**
	 * Singleton constructor.
	 * 
	 * @param name
	 *            Name of the database to load model from.
	 * @throws java.lang.Exception
	 */
	// TODO find more specific exception(s) to throw
	protected Model(final String name) throws Exception {
		final String dbName = this.getDatabaseName(name);
		final File dbFile = new File(dbName);
		final boolean fileExists = dbFile.exists();
		// init SQLite
		try {
			Class.forName("SQLite.JDBCDriver").newInstance();
			Model.conn = DriverManager.getConnection("jdbc:sqlite:/" + dbName);
			final java.lang.reflect.Method m = Model.conn.getClass().getMethod(
					"getSQLiteDatabase", (Class<?>[]) null);
			Model.db = (SQLite.Database) m.invoke(Model.conn, (Object[]) null);
		} catch (final Exception e) {
			throw new Exception("Failed instantiating the model.", e);
		}
		// perform loading of the model
		// TODO uncomment once done debugging the model conversion
		/*
		 * if (fileExists) { System.out.println("Model loaded."); } else {
		 */
		try {
			this.createDb();
		} catch (final FileNotFoundException e) {
			throw new java.lang.Exception(
					"Failed loading input files, cannot create model.", e);
		}
		// }
	}

	private void createDb() throws FileNotFoundException, ParseException,
			IOException {
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
			// process machines
			this.parseMachines(new BufferedReader(new FileReader(mFile)));
			// process dataset
			this.parseDataSet(new BufferedReader(new FileReader(dsFile)));
		} catch (final ParseException e) {
			throw new ParseException("Error while parsing input files.", e
					.getErrorOffset());
		} catch (final IOException e) {
			throw new IOException("Error while reading input files.", e);
		}
	}

	private String getDatabaseName(final String name) {
		return name + ".sqlite";
	}

	private Integer parseDataSet(final BufferedReader reader)
			throws ParseException {
		return 0;
	}

	private Integer parseMachines(final BufferedReader reader)
			throws ParseException {
		return 0;
	}

}
