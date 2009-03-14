/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;
import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;
import cz.muni.fi.spc.SchedVis.ui.GroupPanel;
import cz.muni.fi.spc.SchedVis.ui.MachinePanel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleTreeCellRenderer extends DefaultTreeCellRenderer {

    private static ExecutorService e = Executors.newCachedThreadPool();

    /**
     * 
     */
    private static final long serialVersionUID = -5148385915562957149L;

    private JPanel getGroup(final MachineGroup item, final boolean showDetailed) {
	try {
	    GroupPanel target = new GroupPanel();
	    if (item == null) {
		target.add(new JLabel("Ungrouped Machines"));
	    } else {
		target.add(new JLabel("Group '" + item.getName() + "' of "
			+ item.getMachines().size() + " machines."));
	    }
	    return target;
	} catch (final Exception e) {
	    final JPanel p = new JPanel();
	    p.add(new JLabel("Wrong panel!"));
	    return p;
	}
    }

    private JPanel getMachine(final Machine item) {
	try {
	    MachineRenderer mr = new MachineRenderer(item, TimelineSliderModel
		    .getInstance().getValue());
	    ScheduleTreeCellRenderer.e.submit(mr);
	    final MachinePanel pane = new MachinePanel();
	    pane.setToolTipText("Machine: " + item.getName() + ", time: "
		    + this.getClass());
	    pane.setImage(mr.get());
	    return pane;
	} catch (final Exception e) {
	    final JPanel p = new JPanel();
	    p.add(new JLabel("Wrong panel!"));
	    return p;
	}
    }

    private JPanel getNoGroup(final boolean showDetailed) {
	return this.getGroup(null, showDetailed);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
	    final Object value, final boolean sel, final boolean expanded,
	    final boolean leaf, final int row, final boolean hasFocus) {
	final Object userObject = ((DefaultMutableTreeNode) value)
		.getUserObject();
	if (userObject instanceof Machine) { // is a machine
	    return this.getMachine((Machine) userObject);
	} else if (userObject instanceof MachineGroup) { // is a group
	    return this.getGroup((MachineGroup) userObject, expanded);
	} else if (ScheduleTreeModel.ID_UNGROUPED.equals(userObject)) { // "ungrouped"
	    // group
	    return this.getNoGroup(expanded);
	} else {
	    final JPanel p = new JPanel();
	    p.add(new JLabel("Unknown object!"));
	    return p;
	}
    }

}
