/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.MachineEntity;

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
		ResultSet rs = null;
		if (this.groupId == null) {
			rs = MachineEntity.getAllUngrouped();
		} else if (this.groupId != -1) {
			rs = MachineEntity.getAllInGroup(this.groupId);
		}
		this.removeAllElements();
		try {
			if (rs == null) {
				return;
			}
			while (rs.next()) {
				this.addElement(rs.getObject("name"));
			}
		} catch (final SQLException e) {
			// do nothing
		}
	}

}
