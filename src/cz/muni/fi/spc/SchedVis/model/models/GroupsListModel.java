/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.EntitySet;
import cz.muni.fi.spc.SchedVis.model.entities.GroupEntity;

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
		final EntitySet<GroupEntity> set = GroupEntity.getAllGroups();
		this.removeAllElements();
		for (final GroupEntity item : set) {
			this.addElement(item.getFieldAsString("name"));
		}
	}

}
