/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

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
		this.removeAllElements();
		this.addElement("No group");
		for (final MachineGroup item : MachineGroup.getAll()) {
			this.addElement(item.getName());
		}
	}

}
