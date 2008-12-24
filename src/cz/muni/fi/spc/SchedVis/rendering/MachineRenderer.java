/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MachineRenderer extends Thread {

    private final Machine m;
    private final Integer clock;
    private final JPanel target;

    /**
     * 
     */
    public MachineRenderer(final Machine m, final Integer clock,
	    final JPanel target) {
	super();
	this.m = m;
	this.clock = clock;
	this.target = target;
    }

    public Integer getClock() {
	return this.clock;
    }

    public Machine getMachine() {
	return this.m;
    }

    public JPanel getTarget() {
	return this.target;
    }

    @Override
    public void run() {
	this.target.add(new JLabel(this.m.getName() + "@" + this.clock));
    }

}
