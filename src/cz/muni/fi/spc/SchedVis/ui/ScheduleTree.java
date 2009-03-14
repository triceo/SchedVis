/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import javax.swing.JTree;

import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;
import cz.muni.fi.spc.SchedVis.rendering.ScheduleTreeCellRenderer;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleTree extends JTree {

    private static ScheduleTree tree = null;

    /**
     * 
     */
    private static final long serialVersionUID = -5619978578879924763L;

    public static ScheduleTree getInstance() {
	if (ScheduleTree.tree == null) {
	    ScheduleTree.tree = new ScheduleTree();
	}
	return ScheduleTree.tree;
    }

    /**
     * @param newModel
     */
    private ScheduleTree() {
	super();
	this.setCellRenderer(new ScheduleTreeCellRenderer());
	this.setEditable(false);
	this.setRootVisible(false);
	this.setModel(ScheduleTreeModel.getInstance());
    }

}
