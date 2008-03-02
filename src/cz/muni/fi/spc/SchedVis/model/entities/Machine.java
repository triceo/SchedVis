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
public class Machine extends Entity {

	public static boolean addToGroup(final String machineId,
			final Integer groupId) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("UPDATE machines SET id_machine_groups = ? WHERE id_machines = ?;");
			stmt.setInt(1, groupId);
			stmt.setString(2, machineId);
			if (stmt.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (final SQLException e) {
			return false;
		}
	}

	public static ResultSet getAllInGroup(final Integer groupId) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT * FROM machines WHERE id_machine_groups = ? ORDER BY id_machines ASC;");
			stmt.setInt(1, groupId);
			return stmt.executeQuery();
		} catch (final SQLException e) {
			return null;
		}
	}

	public static ResultSet getAllUngrouped() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT * FROM machines WHERE id_machine_groups IS NULL ORDER BY id_machines ASC;");
			return stmt.executeQuery();
		} catch (final SQLException e) {
			return null;
		}
	}

	public static boolean removeFromGroup(final String machineId) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("UPDATE machines SET id_machine_groups = NULL WHERE id_machines = ?;");
			stmt.setString(1, machineId);
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
	public Machine() {
		super();
	}

}