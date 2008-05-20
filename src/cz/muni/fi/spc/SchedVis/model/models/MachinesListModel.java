/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.EntitySet;
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
		EntitySet<MachineEntity> set = null;
		if (this.groupId == null) {
			set = MachineEntity.getAllUngrouped();
		} else if (this.groupId != -1) {
			set = MachineEntity.getAllInGroup(this.groupId);
		}
		this.removeAllElements();
		if (set == null) return;
		for (final MachineEntity item : set) {
			this.addElement(item.getFieldAsString("name"));
		}
	}

}
