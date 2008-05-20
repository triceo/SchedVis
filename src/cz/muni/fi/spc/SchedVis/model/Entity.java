/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public abstract class Entity implements Cloneable {

	protected static Integer INVALID_KEY_VALUE = -1;

	protected static Connection getConnection() {
		return SQL.getInstance().getConnection();
	}

	protected static PreparedStatement getStatement(final String query)
			throws SQLException {
		return Entity.getConnection().prepareStatement(query);
	}

	private final String table;

	private final String idColumn;

	private ResultSet rs = null;

	public Entity(final String table, final String idColumn) {
		this.table = table;
		this.idColumn = idColumn;
	}

	public Integer getFieldAsInt(final String field) {
		try {
			final Integer fieldId = this.getFieldId(field);
			return this.rs.getInt(fieldId);
		} catch (final SQLException e) {
			return null;
		}
	}

	public String getFieldAsString(final String field) {
		try {
			final Integer fieldId = this.getFieldId(field);
			return this.rs.getString(fieldId);
		} catch (final Exception e) {
			return null;
		}
	}

	private Integer getFieldId(final String field) {
		try {
			return this.rs.findColumn(field);
		} catch (final Exception e) {
			return null;
		}
	}

	public Integer getId() {
		try {
			return this.rs.getInt(1);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean load(final Integer id) {
		if (this.rs != null) {
			return false;
		}
		try {
			final PreparedStatement stmt = Entity.getStatement("SELECT * FROM "
					+ this.table + " WHERE " + this.idColumn + " = ?");
			stmt.setInt(1, id);
			this.rs = stmt.executeQuery();
			this.rs.next();
			return true;
		} catch (final SQLException e) {
			return false;
		}
	}
}
