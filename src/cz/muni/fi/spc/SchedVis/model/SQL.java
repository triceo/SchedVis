/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.AbstractMap;
import java.util.HashMap;

import SQLite.Database;

/**
 * Base class for SchedVis' data model. Uses SQLite as its backend. It is a
 * singleton as we'll never need more than 1 model.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class SQL {

	private static AbstractMap<String, SQL> instances = new HashMap<String, SQL>();
	private static SQL lastInstance = null;
	private static Connection conn = null;
	private static Database db = null;

	/**
	 * Return last used instance of this class.
	 * 
	 * @return
	 */
	public static SQL getInstance() {
		return SQL.lastInstance;
	}

	/**
	 * Get instance of this class.
	 * 
	 * @param name
	 *            Name of the model to load.
	 * @return The model.
	 * @throws java.lang.Exception
	 */
	public static SQL getInstance(final String name, final boolean newDatabase)
			throws java.lang.Exception {
		if (!SQL.instances.containsKey(name)
				|| (SQL.instances.get(name) == null)) {
			SQL.instances.put(name, new SQL(name, newDatabase));
		}
		SQL.lastInstance = SQL.instances.get(name);
		return SQL.getInstance();
	}

	/**
	 * Singleton constructor.
	 * 
	 * @param name
	 *            Name of the database to load model from.
	 * @throws java.lang.Exception
	 * @todo implement some schema-validating tool
	 * @todo find more specific exception(s) to throw
	 */
	protected SQL(final String name, boolean newDatabase) throws Exception {
		// initialize SQLite
		final File file = new File(name + ".sqlite");
		if (newDatabase && file.exists()) {
			throw new Exception("Database already exists.");
		} else if (!newDatabase && !file.exists()) {
			throw new Exception("Database does not exist yet!");
		}
		try {
			Class.forName("SQLite.JDBCDriver").newInstance();
			SQL.conn = DriverManager.getConnection("jdbc:sqlite:/" + name
					+ ".sqlite");
			final java.lang.reflect.Method m = SQL.conn.getClass().getMethod(
					"getSQLiteDatabase", (Class<?>[]) null);
			SQL.db = (SQLite.Database) m.invoke(SQL.conn, (Object[]) null);
		} catch (final Exception e) {
			throw new Exception("Failed instantiating the model.", e);
		}
	}

	@Override
	public void finalize() throws Throwable {
		SQL.conn.close();
		SQL.db.close();
		super.finalize();
	}

	public Connection getConnection() {
		return SQL.conn;
	}

}
