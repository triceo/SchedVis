/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cz.muni.fi.spc.SchedVis.model.entities.GroupEntity;
import cz.muni.fi.spc.SchedVis.model.entities.MachineEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5148385915562957149L;

	/**
	 * 
	 */
	public ScheduleTreeCellRenderer() {
		// TODO Auto-generated constructor stub
	}

	public JPanel getGroup(final Integer id, final boolean showDetailed) {
		final JPanel pane = new JPanel();
		if (id == -1) {
			pane.add(new JLabel("Ungrouped machines"));
		} else {
			pane.add(new JLabel(GroupEntity.getNameWithId(id)));
		}
		return pane;
	}

	public JPanel getMachine(final Integer id) {
		final JPanel pane = new JPanel();
		pane.add(new JLabel(MachineEntity.getNameWithId(id)));
		return pane;
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree,
			final Object value, final boolean sel, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		if (value.toString() == null) {
			return new JPanel(); // skip empties
		}
		if (leaf) { // is a machine
			return this.getMachine(new Integer(value.toString()));
		} else { // is a group
			return this.getGroup(new Integer(value.toString()), expanded);
		}
	}

}
