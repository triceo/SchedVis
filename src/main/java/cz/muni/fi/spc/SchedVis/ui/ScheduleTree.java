/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.Cursor;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cz.muni.fi.spc.SchedVis.Main;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;

/**
 * The tree to display schedules in. It is a singleton, no more instances
 * needed.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleTree extends JTree implements TreeSelectionListener {

	private static ScheduleTree tree = null;

	private static final long serialVersionUID = -5619978578879924763L;

	/**
	 * Get the only instance of the class.
	 * 
	 * @return The tree.
	 */
	public static ScheduleTree getInstance() {
		if (ScheduleTree.tree == null) {
			ScheduleTree.tree = new ScheduleTree();
			ScheduleTree.tree.addTreeSelectionListener(ScheduleTree.tree);
		}
		return ScheduleTree.tree;
	}

	/**
	 * The constructor.
	 */
	private ScheduleTree() {
		super();
		this.setExpandsSelectedPaths(true);
		this.setShowsRootHandles(true);
		this.setCellRenderer(new ScheduleTreeCellRenderer());
		this.setEditable(false);
		this.setRootVisible(false);
		this.setOpaque(true);
		this.setModel(ScheduleTreeModel.getInstance());
	}

	/**
	 * Listens to changes in selection on this tree.
	 */
	public void valueChanged(final TreeSelectionEvent e) {
		Main.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			final Object o = ((DefaultMutableTreeNode) ScheduleTree.tree
			    .getLastSelectedPathComponent()).getUserObject();
			if (o instanceof Machine) {
				Main.getFrame().updateDetail((Machine) o);
			} else {
				Main.getFrame().updateDetail(null);
			}
		} catch (final NullPointerException ex) {
			Main.getFrame().updateDetail(null);
		}
		Main.getFrame()
		    .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
