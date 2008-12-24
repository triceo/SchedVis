/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class GroupRenderer implements Callable<JPanel> {

    private final MachineGroup g;
    private final Integer clock;
    private final JPanel target;

    /**
     * 
     */
    public GroupRenderer(final MachineGroup g, final Integer clock,
	    final JPanel target) {
	super();
	this.g = g;
	this.clock = clock;
	this.target = target;
    }

    @Override
    public JPanel call() {
	if (this.g == null) {
	    this.target.add(new JLabel("UngroupedMachines@" + this.clock));
	} else {
	    this.target.add(new JLabel(this.g.getName() + "@" + this.clock));
	}
	return this.target;
    }

    public Integer getClock() {
	return this.clock;
    }

    public MachineGroup getGroup() {
	return this.g;
    }

}
