/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class RenderMachine implements Runnable {

    private static Executor e = null;

    public static Executor getExecutor() {
	if (RenderMachine.e == null) {
	    RenderMachine.e = Executors.newCachedThreadPool();
	}
	return RenderMachine.e;
    }

    private final Machine m;
    private final Integer clock;
    private final JPanel target;

    /**
	 * 
	 */
    public RenderMachine(final Machine m, final Integer clock,
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
	this.target.add(new JLabel(this.m.getName()));
	Main.update();
    }

}
