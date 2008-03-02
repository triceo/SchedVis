/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

import cz.muni.fi.spc.SchedVis.model.models.MachinesListModel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupedMachinesList extends JList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 157627105166533902L;

	/**
	 * 
	 */
	public GroupedMachinesList(final Integer groupId,
			ListSelectionListener owner) {
		this.setModel(new MachinesListModel(groupId, null));
		this.update();
		this.addListSelectionListener(owner);
	}

	public void update() {
		final MachinesListModel model = (MachinesListModel) this.getModel();
		model.update();
	}
}
