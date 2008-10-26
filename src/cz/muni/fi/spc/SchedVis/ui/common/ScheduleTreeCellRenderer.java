/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;
import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;

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

	public JPanel getGroup(final MachineGroup item, final boolean showDetailed) {
		final JPanel pane = new JPanel();
		pane.add(new JLabel(item.getName()));
		return pane;
	}

	public JPanel getMachine(final Machine item) {
		final JPanel pane = new JPanel();
		pane.add(new JLabel(item.getName()));
		return pane;
	}

	public JPanel getNoGroup(final boolean showDetailed) {
		final JPanel pane = new JPanel();
		pane.add(new JLabel("Ungrouped machines"));
		return pane;
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree,
			final Object value, final boolean sel, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		final Object userObject = this
				.getUserObject((DefaultMutableTreeNode) value);
		if (userObject instanceof Machine) { // is a machine
			return this.getMachine((Machine) userObject);
		} else if (userObject instanceof MachineGroup) { // is a group
			return this.getGroup((MachineGroup) userObject, expanded);
		} else if (ScheduleTreeModel.ID_UNGROUPED.equals(userObject)) { // "ungrouped"
			// group
			return this.getNoGroup(expanded);
		} else {
			return new JPanel();
		}
	}

	private Object getUserObject(final DefaultMutableTreeNode node) {
		return node.getUserObject();
	}

}
