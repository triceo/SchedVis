/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

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
		this.removeAllElements();
		for (final Machine item : Machine.getAll(this.groupId)) {
			this.addElement(item.getName());
		}
	}

}
