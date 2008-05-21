/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import java.util.AbstractSet;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import cz.muni.fi.spc.SchedVis.model.EntitySet;
import cz.muni.fi.spc.SchedVis.model.entities.GroupEntity;
import cz.muni.fi.spc.SchedVis.model.entities.MachineEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleTreeModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5555189641185105899L;
	private static ScheduleTreeModel model;

	public static final Integer ID_UNGROUPED = -1;

	public static ScheduleTreeModel getInstance() {
		if (ScheduleTreeModel.model == null) {
			ScheduleTreeModel.model = new ScheduleTreeModel(ScheduleTreeModel
					.getTree());
		}
		return ScheduleTreeModel.model;
	}

	private static DefaultMutableTreeNode getTree() {
		final EntitySet<GroupEntity> set = GroupEntity.getAllGroups();
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (final GroupEntity item : set) {
			final DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
			final EntitySet<MachineEntity> set2 = MachineEntity
					.getAllInGroup(item.getId());
			for (final MachineEntity item2 : set2) {
				node.add(new DefaultMutableTreeNode(item2));
			}
			root.add(node);
		}
		final EntitySet<MachineEntity> set3 = MachineEntity.getAllUngrouped();
		final DefaultMutableTreeNode ungroupedNode = new DefaultMutableTreeNode(
				ScheduleTreeModel.ID_UNGROUPED);
		for (final MachineEntity item : set3) {
			ungroupedNode.add(new DefaultMutableTreeNode(item));
		}
		if (ungroupedNode.getChildCount() > 0) {
			root.add(ungroupedNode);
		}
		return root;
	}

	private ScheduleTreeModel() {
		super(new DefaultMutableTreeNode());
		this.specialize();
	}

	/**
	 * @param root
	 */
	private ScheduleTreeModel(final TreeNode root) {
		super(root);
		this.specialize();
	}

	/**
	 * @param root
	 * @param asksAllowsChildren
	 */
	private ScheduleTreeModel(final TreeNode root,
			final boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
		this.specialize();
	}

	/**
	 * Update the tree model so that it contains only the groups received in the
	 * argument. Also update sub-models for these groups.
	 * 
	 * @param visibleGroups
	 */
	@SuppressWarnings( { "unchecked" })
	public void regroup(final AbstractSet<Integer> visibleGroups) {
		final DefaultMutableTreeNode root = ScheduleTreeModel.getTree();
		if (!visibleGroups.isEmpty()) {
			final boolean allowUngrouped = visibleGroups.contains(-1);
			Enumeration<DefaultMutableTreeNode> e = root.children();
			while (e.hasMoreElements()) {
				final DefaultMutableTreeNode node = e.nextElement();
				final boolean isUngrouped = allowUngrouped ? (node
						.getUserObject().equals(ScheduleTreeModel.ID_UNGROUPED))
						: false;
				final boolean isInSet = isUngrouped ? false : visibleGroups
						.contains(((GroupEntity) node.getUserObject()).getId());
				if (!(isInSet || isUngrouped)) {
					this.removeNodeFromParent(node);
					e = root.children();
				}
			}
		}
		this.setRoot(root);
	}

	private void specialize() {
	}

}
