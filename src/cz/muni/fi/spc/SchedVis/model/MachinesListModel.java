/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MachinesListModel extends DefaultListModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7269134473621539118L;

	private final Integer groupId;

	/**
	 * 
	 */
	public MachinesListModel(final Integer groupId,
			final ListDataListener listener) {
		this.groupId = groupId;
		this.update();
		this.addListDataListener(listener);
	}

	public void update() {
		ResultSet rs;
		if (this.groupId == null) {
			rs = MachineEntity.getAllUngrouped();
		} else {
			rs = MachineEntity.getAllInGroup(this.groupId);
		}
		try {
			this.removeAllElements();
			while (rs.next()) {
				this.addElement(rs.getObject("id_machines"));
			}
		} catch (final SQLException e) {
			// do nothing
		}
	}

}
