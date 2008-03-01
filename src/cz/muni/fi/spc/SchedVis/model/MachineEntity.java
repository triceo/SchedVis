/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MachineEntity extends Entity {

	public static ResultSet getAllUngrouped() {
		try {
			final PreparedStatement stmt = Entity.getStatement("SELECT * FROM machines WHERE id_machine_groups IS NULL ORDER BY id_machines ASC;");
			return stmt.executeQuery();
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static ResultSet getAllInGroup(Integer groupId) {
		try {
			final PreparedStatement stmt = Entity.getStatement("SELECT * FROM machines WHERE id_machine_groups = ? ORDER BY id_machines ASC;");
			stmt.setInt(1, groupId);
			return stmt.executeQuery();
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static boolean addToGroup(String machineId, Integer groupId) {
		try {
			final PreparedStatement stmt = Entity.getStatement("UPDATE machines SET id_machine_groups = ? WHERE id_machines = ?;");
			stmt.setInt(1, groupId);
			stmt.setString(2, machineId);
			if (stmt.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}		
	}
	
	public static boolean removeFromGroup(String machineId) {
		try {
			final PreparedStatement stmt = Entity.getStatement("UPDATE machines SET id_machine_groups = NULL WHERE id_machines = ?;");
			stmt.setString(1, machineId);
			if (stmt.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
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
