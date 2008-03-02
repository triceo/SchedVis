/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.Group;

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
		final ResultSet rs = Group.getAllGroups();
		try {
			this.removeAllElements();
			while (rs.next()) {
				this.addElement(rs.getObject("name"));
			}
		} catch (final SQLException e) {
			// do nothing
		}
	}

}
