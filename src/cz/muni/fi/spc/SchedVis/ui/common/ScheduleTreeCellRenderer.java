/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

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

	@Override
	public Component getTreeCellRendererComponent(final JTree tree,
			final Object value, final boolean sel, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		final JPanel panel = new JPanel();
		panel.add(new JLabel(value.toString()));
		return panel;
	}

}
