/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.MachinePanel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MachineRenderer implements Callable<JPanel> {

    private final Machine m;

    private final Integer clock;

    private static final Integer NUM_PIXELS_PER_CPU = 4;

    private static final Double NUM_PIXELS_PER_TICK = 0.05;
    private static final Double LINE_WIDTH = Event.getMaxJobSpan()
    * MachineRenderer.NUM_PIXELS_PER_TICK;
    private static final Integer MIN_JOB_LENGTH_PIXELS = 4;

    private static final Color[] colors = {
	Color.BLUE,
	Color.DARK_GRAY,
	Color.CYAN,
	Color.GREEN,
	Color.MAGENTA,
	Color.LIGHT_GRAY,
	Color.ORANGE,
	Color.PINK,
	Color.RED,
	Color.GRAY,
	Color.YELLOW };

    /**
     * 
     */
    public MachineRenderer(final Machine m, final Integer clock) {
	super();
	this.m = m;
	this.clock = clock;
    }

    @Override
    public JPanel call() {
	final BufferedImage img = new BufferedImage(
		MachineRenderer.LINE_WIDTH
		.intValue(), this.m.getCPUs()
		* MachineRenderer.NUM_PIXELS_PER_CPU,
		BufferedImage.TYPE_INT_RGB);
	final Graphics2D g = (Graphics2D) img.getGraphics();
	g.setColor(Color.BLACK);
	g.draw(new Rectangle(0, 0, img.getWidth() - 1, img.getHeight() - 1));
	g.setColor(Color.WHITE);
	g.fill(new Rectangle(1, 1, img.getWidth() - 2, img.getHeight() - 2));
	this.drawJobs(img);
	final MachinePanel pane = new MachinePanel();
	pane.setToolTipText("Machine: " + this.m.getName() + ", time: "
		+ this.getClass());
	pane.setImage(img);
	return pane;
    }

    private void drawJobs(final Image img) {
	final Graphics2D g = (Graphics2D) img.getGraphics();
	// render jobs in a schedule, one by one
	Iterator<Color> it = this.getColorIterator();
	for (final Event evt: Machine.getLatestSchedule(this.m, this.clock)) {
	    // assign color to the job
	    if (!it.hasNext()) {
		it = this.getColorIterator();
	    }
	    final Color currentColor = it.next();
	    // get starting/ending coordinates
	    final Double jobStartX = this.getStartingPosition(evt);
	    final Double jobLength = this.getJobLength(evt);
	    // get assigned CPUs, set will ensure they are unique
	    final Set<Integer> assignedCPUs = new HashSet<Integer>();
	    if (evt.getAssignedCPUs() == null) {
		System.out.println("Event:" + evt.getId());
	    }
	    for (final String num : evt.getAssignedCPUs().split(",")) {
		assignedCPUs.add(new Integer(num));
	    }
	    System.out.println(this.m.getName() + " " + this.m.getCPUs() + " "
		    + assignedCPUs);
	    /*
	     * now isolate all the contiguous blocks of CPUs in the job and
	     * paint them.
	     */
	    final Integer[] cpus = assignedCPUs.toArray(new Integer[] {});
	    for (Integer i = 0; i < cpus.length; i++) {
		final Integer currentCPU = cpus[i];
		try {
		    while (cpus[i + 1] == cpus[i] + 1) {
			// loop until a gap is found in the list of used CPUs
			i++;
		    }
		} catch (final ArrayIndexOutOfBoundsException e) {
		    // but finish when all the CPUs have been seeked through
		}
		final Integer lastCPU = cpus[i];
		final Integer numCPUs = lastCPU - currentCPU + 1;
		// now draw
		final Double leftTopX = 0 + jobStartX;
		final Integer leftTopY = currentCPU
		* MachineRenderer.NUM_PIXELS_PER_CPU;
		g.setColor(Color.BLACK);
		g.draw(new Rectangle(leftTopX.intValue(), leftTopY,
			jobLength.intValue(),
			new Integer(numCPUs
				* MachineRenderer.NUM_PIXELS_PER_CPU)));
		g.setColor(currentColor);
		g.fill(new Rectangle(leftTopX.intValue() + 1, leftTopY + 1,
			jobLength.intValue() - 1, new Integer(numCPUs
				* MachineRenderer.NUM_PIXELS_PER_CPU) - 1));
	    }
	}
    }

    public Integer getClock() {
	return this.clock;
    }

    private Iterator<Color> getColorIterator() {
	return Arrays.asList(MachineRenderer.colors).iterator();
    }

    private Double getJobLength(final Event evt) {
	try {
	    return Math.max((evt.getExpectedEnd() - evt
		    .getExpectedStart())
		    * MachineRenderer.NUM_PIXELS_PER_TICK,
		    MachineRenderer.MIN_JOB_LENGTH_PIXELS);
	} catch (final NullPointerException e) {
	    return new Double(MachineRenderer.MIN_JOB_LENGTH_PIXELS);
	}
    }

    public Machine getMachine() {
	return this.m;
    }

    /**
     * Get the starting position for the event, when being rendered on the
     * screen.
     * 
     * @param evt
     *            The event in question.
     * @return The X starting coordinate.
     */
    private Double getStartingPosition(final Event evt) {
	try {
	    // return (evt.getExpectedStart() - this.clock) *
	    // MachineRenderer.NUM_PIXELS_PER_TICK;
	    return evt.getExpectedStart() * MachineRenderer.NUM_PIXELS_PER_TICK;
	} catch (final NullPointerException e) {
	    return 0.0;
	}
    }

}
