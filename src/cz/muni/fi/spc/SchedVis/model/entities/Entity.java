/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.muni.fi.spc.SchedVis.model.SQL;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Entity {

	protected static Integer INVALID_KEY_VALUE = -1;

	protected static Connection getConnection() {
		return SQL.getInstance().getConnection();
	}

	protected static PreparedStatement getStatement(final String query)
			throws SQLException {
		return Entity.getConnection().prepareStatement(query);
	}

}
