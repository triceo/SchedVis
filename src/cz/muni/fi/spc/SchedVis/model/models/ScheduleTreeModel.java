/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractSet;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

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

	private static final String ID_UNGROUPED = "Ungrouped machines";

	public static ScheduleTreeModel getInstance() {
		if (ScheduleTreeModel.model == null) {
			ScheduleTreeModel.model = new ScheduleTreeModel(ScheduleTreeModel
					.getTree());
		}
		return ScheduleTreeModel.model;
	}

	private static DefaultMutableTreeNode getTree() {
		final ResultSet rs = GroupEntity.getAllGroups();
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		try {
			while (rs.next()) {
				final DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						rs.getString(2));
				final ResultSet rs2 = MachineEntity.getAllInGroup(rs.getInt(1));
				while (rs2.next()) {
					node.add(new DefaultMutableTreeNode(rs2.getString(2)));
				}
				root.add(node);
			}
			final ResultSet rs3 = MachineEntity.getAllUngrouped();
			final DefaultMutableTreeNode ungroupedNode = new DefaultMutableTreeNode(
					ScheduleTreeModel.ID_UNGROUPED);
			while (rs3.next()) {
				ungroupedNode.add(new DefaultMutableTreeNode(rs3.getString(2)));
			}
			if (ungroupedNode.getChildCount() > 0) {
				root.add(ungroupedNode);
			}
		} catch (final SQLException e) {
			// intentionally blank
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
	@SuppressWarnings({ "unchecked" })
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
						.contains(GroupEntity.getIdWithName((String) node
								.getUserObject()));
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
