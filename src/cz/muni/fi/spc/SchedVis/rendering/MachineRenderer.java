/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MachineRenderer implements Callable<JPanel> {

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

    @Override
    public JPanel call() {
	this.target.add(new JLabel(this.m.getName() + "@" + this.clock));
	return this.target;
    }

    public Integer getClock() {
	return this.clock;
    }

    public Machine getMachine() {
	return this.m;
    }

}
