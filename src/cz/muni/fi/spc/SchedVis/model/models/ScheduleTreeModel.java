/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import java.util.AbstractSet;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

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
	    Enumeration<DefaultMutableTreeNode> e = root.children();
	    while (e.hasMoreElements()) {
		final DefaultMutableTreeNode node = e.nextElement();
		boolean isInSet = false;
		if (node.getUserObject() instanceof BaseEntity) {
		    if (visibleGroups.contains(((MachineGroup) node
			    .getUserObject()).getId())) {
			isInSet = true;
		    }
		} else {
		    if (visibleGroups.contains(new Integer(
			    ScheduleTreeModel.ID_UNGROUPED))) {
			isInSet = true;
		    }
		}
		if (!isInSet) {
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
