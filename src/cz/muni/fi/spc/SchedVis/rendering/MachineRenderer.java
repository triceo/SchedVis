/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MachineRenderer extends SwingWorker<Image, Void> {

    private final Machine m;

    private static final Long instanceId = Math.round(Math.random() * 100000);

    private final Integer clock;
    private final Integer tickOffset;

    private static final Integer NUM_PIXELS_PER_CPU = 4;

    private static final Float NUM_PIXELS_PER_TICK = new Float(0.05);
    private static final Integer LINE_WIDTH = Math.round(Event.getMaxJobSpan()
	    * MachineRenderer.NUM_PIXELS_PER_TICK);
    private static final Integer MIN_JOB_LENGTH_PIXELS = 4;

    private static final Color[] colors = { Color.BLUE, Color.CYAN,
	Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
	Color.YELLOW };

    private static final Map<Integer, Color> jobColors = new HashMap<Integer, Color>();
    private static final Map<Machine, Map<Integer, File>> files = new HashMap<Machine, Map<Integer, File>>();

    /**
     * 
     */
    public MachineRenderer(final Machine m, final Integer clock) {
	this.m = m;
	this.clock = clock;
	this.tickOffset = Event.getMinExpectedStartTime(this.clock);
    }

    private BufferedImage actuallyDraw() {
	final BufferedImage img = new BufferedImage(MachineRenderer.LINE_WIDTH
		.intValue(), this.m.getCPUs()
		* MachineRenderer.NUM_PIXELS_PER_CPU,
		BufferedImage.TYPE_INT_RGB);
	final Graphics2D g = (Graphics2D) img.getGraphics();
	boolean isActive = Machine.isActive(this.m, this.clock);
	if (isActive) {
	    g.setColor(Color.WHITE);
	} else {
	    g.setColor(Color.GRAY);
	}
	g.fill(new Rectangle(1, 1, img.getWidth() - 2, img.getHeight() - 2));
	this.drawJobs(img);
	g.setColor(Color.BLACK);
	g.draw(new Rectangle(0, 0, img.getWidth() - 1, img.getHeight() - 1));
	if (isActive) {
	    g.setColor(Color.BLACK);
	} else {
	    g.setColor(Color.WHITE);
	}
	g.setFont(new Font("Monospaced", Font.PLAIN, 10));
	g.drawString(this.m.getName(), 2, 10);
	return img;
    }

    @Override
    public Image doInBackground() {
	if (!MachineRenderer.files.containsKey(this.m)) {
	    MachineRenderer.files.put(this.m, new HashMap<Integer, File>());
	}
	Map<Integer, File> filePerClock = MachineRenderer.files.get(this.m);
	if (!filePerClock.containsKey(this.clock)) {
	    boolean dontWrite = false;
	    File f = null;
	    try {
		f = File.createTempFile("schedvis"
			+ MachineRenderer.instanceId + "-t" + this.clock + "m"
			+ this.m.getId() + ".", ".gif");
	    } catch (IOException e) {
		Logger.getLogger(MachineRenderer.class).warn(
			"Won't cache machine " + this.m.getId() + " at "
			+ this.clock
			+ ". Failed to create a temp file.");
		dontWrite = true;
	    }
	    BufferedImage img = this.actuallyDraw();
	    if (!dontWrite) {
		try {
		    ImageIO.write(img, "gif", f);
		    filePerClock.put(this.clock, f);
		} catch (IOException e) {
		    Logger.getLogger(MachineRenderer.class).warn(
			    "Won't cache machine " + this.m.getId() + " at "
			    + this.clock
			    + ". Failed to write into a temp file.");
		}
	    }
	    return img;
	} else {
	    try {
		return ImageIO.read(filePerClock.get(this.clock));
	    } catch (IOException e) {
		Logger.getLogger(MachineRenderer.class).warn(
			"Cannot read cache for machine " + this.m.getId()
			+ " at " + this.clock
			+ ". Failed to write into a temp file.");
		return this.actuallyDraw();
	    }
	}
    }

    /**
     * 
     * @param img
     * @todo Produces unclear job boundaries, probably because of rounding.
     */
    private void drawJobs(final Image img) {
	final Graphics2D g = (Graphics2D) img.getGraphics();
	// render jobs in a schedule, one by one
	for (final Event evt : Machine.getLatestSchedule(this.m, this.clock)) {
	    // get starting/ending coordinates
	    int jobStartX = this.getStartingPosition(evt);
	    int jobLength = this.getJobLength(evt);
	    // get assigned CPUs, set will ensure they are unique
	    final Set<Integer> assignedCPUs = new HashSet<Integer>();
	    for (final String num : evt.getAssignedCPUs().split(",")) {
		assignedCPUs.add(new Integer(num));
	    }
	    /*
	     * now isolate all the contiguous blocks of CPUs in the job and
	     * paint them.
	     */
	    final Integer[] cpus = assignedCPUs.toArray(new Integer[] {});
	    for (int i = 0; i < cpus.length; i++) {
		final Integer crntCPU = cpus[i];
		try {
		    while (cpus[i + 1] == cpus[i] + 1) {
			// loop until a gap is found in the list of used CPUs
			i++;
		    }
		} catch (final ArrayIndexOutOfBoundsException e) {
		    // but finish when all the CPUs have been seeked through
		}
		final int lastCPU = cpus[i];
		final int numCPUs = lastCPU - crntCPU + 1;
		// now draw
		final int ltY = crntCPU * MachineRenderer.NUM_PIXELS_PER_CPU;
		final int jobHgt = numCPUs * MachineRenderer.NUM_PIXELS_PER_CPU;
		g.setColor(this.getJobColor(evt.getJob()));
		g.fill3DRect(jobStartX, ltY, jobLength, jobHgt, true);
	    }
	}
    }

    public Integer getClock() {
	return this.clock;
    }

    /**
     * Make sure a job has always the same color, no matter when and where it is
     * painted.
     * 
     * @param jobId
     * @return
     */
    private Color getJobColor(final Integer jobId) {
	if (!MachineRenderer.jobColors.containsKey(jobId)) {
	    MachineRenderer.jobColors.put(jobId, MachineRenderer.colors[jobId
	                                                                % MachineRenderer.colors.length]);
	}
	return MachineRenderer.jobColors.get(jobId);
    }

    private int getJobLength(final Event evt) {
	try {
	    return Math.max(Math.round((evt.getExpectedEnd() - evt
		    .getExpectedStart())
		    * MachineRenderer.NUM_PIXELS_PER_TICK),
		    MachineRenderer.MIN_JOB_LENGTH_PIXELS);
	} catch (final NullPointerException e) {
	    return MachineRenderer.MIN_JOB_LENGTH_PIXELS;
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
    private int getStartingPosition(final Event evt) {
	try {
	    return Math.round((evt.getExpectedStart() - this.tickOffset)
		    * MachineRenderer.NUM_PIXELS_PER_TICK);
	} catch (final NullPointerException e) {
	    return 0;
	}
    }

}
