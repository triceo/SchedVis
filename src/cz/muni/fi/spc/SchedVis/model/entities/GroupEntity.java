/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupEntity extends Entity {

	public static boolean delete(final Integer id) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("UPDATE machines SET id_machine_groups = NULL WHERE id_machine_groups = ?");
			stmt.setInt(1, id);
			stmt.execute();
			final PreparedStatement stmt2 = Entity
					.getStatement("DELETE FROM machine_groups WHERE id_machine_groups = ?;");
			stmt2.setInt(1, id);
			if (stmt2.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (final SQLException e) {
			return false;
		}
	}

	public static ResultSet getAllGroups() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT * FROM machine_groups ORDER BY name ASC;");
			return stmt.executeQuery();
		} catch (final SQLException e) {
			return null;
		}
	}

	public static Integer getIdWithName(final String name) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT id_machine_groups FROM machine_groups WHERE name = ?");
			stmt.setString(1, name);
			final ResultSet rs = stmt.executeQuery();
			if (rs.first()) {
				return rs.getInt(1);
			} else {
				return Entity.INVALID_KEY_VALUE;
			}
		} catch (final SQLException e) {
			return Entity.INVALID_KEY_VALUE;
		}
	}

	public static boolean insert(final String name) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("INSERT INTO machine_groups (name) VALUES (?);");
			stmt.setString(1, name);
			if (stmt.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (final SQLException e) {
			return false;
		}
	}

	/**
	 * 
	 */
	public GroupEntity() {
		super();
	}

}
