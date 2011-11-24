/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

/**
 * A model for the tree of machine schedules. It is a singleton, as the tree is
 * also only one.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = -5555189641185105899L;
	private static ScheduleTreeModel model;

	public static final Integer ID_UNGROUPED = -1;

	/**
	 * Get the only instance of this class.
	 * 
	 * @return The instance.
	 */
	public static ScheduleTreeModel getInstance() {
		if (ScheduleTreeModel.model == null) {
			ScheduleTreeModel.model = new ScheduleTreeModel(ScheduleTreeModel
			    .getTree());
		}
		return ScheduleTreeModel.model;
	}

	/**
	 * Get the complete tree of all the groups and machines.
	 * 
	 * @return The root node of the tree.
	 */
	private static DefaultMutableTreeNode getTree() {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (final MachineGroup item : MachineGroup.getAll()) {
			final DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
			for (final Machine item2 : Machine.getAll(item.getId())) {
				node.add(new DefaultMutableTreeNode(item2));
			}
			root.add(node);
		}
		final DefaultMutableTreeNode ungroupedNode = new DefaultMutableTreeNode(
		    ScheduleTreeModel.ID_UNGROUPED);
		for (final Machine item : Machine.getAll(null)) {
			ungroupedNode.add(new DefaultMutableTreeNode(item));
		}
		if (ungroupedNode.getChildCount() > 0) {
			root.add(ungroupedNode);
		}
		return root;
	}

	/**
	 * Class constructor.
	 * 
	 * @param root
	 *          What to fill the model with.
	 */
	private ScheduleTreeModel(final TreeNode root) {
		super(root);
	}

	/**
	 * Refresh the tree model on the screen.
	 */
	public void regroup() {
		final DefaultMutableTreeNode root = ScheduleTreeModel.getTree();
		this.setRoot(root);
	}

}
