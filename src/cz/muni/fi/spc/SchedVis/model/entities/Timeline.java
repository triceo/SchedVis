/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Timeline extends Entity {

	public static SortedSet<Number> getAllClocks() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT DISTINCT clock FROM events ORDER BY clock ASC;");
			final ResultSet rs = stmt.executeQuery();
			final SortedSet<Number> result = new TreeSet<Number>();
			while (rs.next()) {
				result.add(new Long(Math.round(rs.getDouble("clock"))));
			}
			return result;
		} catch (final SQLException e) {
			return new TreeSet<Number>();
		}
	}

	/**
	 * Get the maximum value of timeline's clock.
	 * 
	 * @return Value on success, maximum possible Double() value on failure.
	 */
	public static Number getMaxClock() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT MAX(clock) FROM events;");
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			return new Long(Math.round(rs.getDouble("MAX(clock)")));
		} catch (final SQLException e) {
			return Long.MAX_VALUE;
		}
	}

	/**
	 * Get the minimum value of timeline's clock.
	 * 
	 * @return Value on success, 0 on failure.
	 */
	public static Number getMinClock() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT MIN(clock) FROM events;");
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			return new Long(Math.round(rs.getDouble("MIN(clock)")));
		} catch (final SQLException e) {
			return 0.0;
		}
	}

	/**
	 * 
	 */
	public Timeline() {
		// TODO Auto-generated constructor stub
	}

}
