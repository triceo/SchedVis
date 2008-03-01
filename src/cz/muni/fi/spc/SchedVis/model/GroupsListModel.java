/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupsListModel extends DefaultComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3334519146045362529L;

	/**
	 * 
	 */
	public GroupsListModel(final ListDataListener listener) {
		this.update();
		this.addListDataListener(listener);
	}

	public void update() {
		final ResultSet rs = GroupEntity.getAllGroups();
		try {
			this.removeAllElements();
			while (rs.next()) {
				this.addElement(rs.getObject("name"));
			}
		} catch (SQLException e) {
			// do nothing
		}
	}

}
