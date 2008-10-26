/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.common;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JScheduleTree extends JTree {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5619978578879924763L;

	/**
	 * 
	 */
	public JScheduleTree() {
		this.specialize();
	}

	/**
	 * @param value
	 */
	public JScheduleTree(final Hashtable<?, ?> value) {
		super(value);
		this.specialize();
	}

	/**
	 * @param value
	 */
	public JScheduleTree(final Object[] value) {
		super(value);
		this.specialize();
	}

	/**
	 * @param newModel
	 */
	public JScheduleTree(final TreeModel newModel) {
		super(newModel);
		this.specialize();
	}

	/**
	 * @param root
	 */
	public JScheduleTree(final TreeNode root) {
		super(root);
		this.specialize();
	}

	/**
	 * @param root
	 * @param asksAllowsChildren
	 */
	public JScheduleTree(final TreeNode root, final boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
		this.specialize();
	}

	/**
	 * @param value
	 */
	public JScheduleTree(final Vector<?> value) {
		super(value);
		this.specialize();
	}

	private void specialize() {
		this.setCellRenderer(new ScheduleTreeCellRenderer());
		this.setEditable(false);
		this.setRootVisible(false);
		this.setModel(ScheduleTreeModel.getInstance());
	}

}
