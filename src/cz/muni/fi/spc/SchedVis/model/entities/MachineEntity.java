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
public class MachineEntity extends Entity {

	public static boolean addToGroup(final Integer machineId,
			final Integer groupId) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("UPDATE machines SET id_machine_groups = ? WHERE id_machines = ?;");
			stmt.setInt(1, groupId);
			stmt.setInt(2, machineId);
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
					.getStatement("SELECT * FROM machines WHERE id_machine_groups = ? ORDER BY name ASC;");
			stmt.setInt(1, groupId);
			return stmt.executeQuery();
		} catch (final SQLException e) {
			return null;
		}
	}

	public static ResultSet getAllUngrouped() {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT * FROM machines WHERE id_machine_groups IS NULL ORDER BY name ASC;");
			return stmt.executeQuery();
		} catch (final SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Integer getIdWithName(final String name) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("SELECT id_machines FROM machines WHERE name = ?");
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

	public static boolean removeFromGroup(final Integer machineId) {
		try {
			final PreparedStatement stmt = Entity
					.getStatement("UPDATE machines SET id_machine_groups = NULL WHERE id_machines = ?;");
			stmt.setInt(1, machineId);
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
	public MachineEntity() {
		super();
	}

}
