/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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

    private static final Integer NUM_PIXELS_PER_CPU = 5;

    private static final Float NUM_PIXELS_PER_TICK = new Float(0.1);
    private static final Integer LINE_WIDTH = Math.round(Event.getMaxJobSpan()
	    * MachineRenderer.NUM_PIXELS_PER_TICK);
    private static final Integer MIN_JOB_LENGTH_PIXELS = 4;

    private static final Color[] colors = { Color.BLUE, Color.CYAN,
	    Color.GREEN, Color.GRAY, Color.MAGENTA, Color.ORANGE,
	    Color.LIGHT_GRAY, Color.PINK, Color.RED, Color.YELLOW };

    private static final Map<Integer, Color> jobColors = new HashMap<Integer, Color>();
    private static final Map<Machine, Map<Integer, File>> files = new HashMap<Machine, Map<Integer, File>>();
    private static final Map<Integer, Integer> tickOffsets = new HashMap<Integer, Integer>();

    private static Font font = new Font("Monospaced", Font.PLAIN, 9);

    private static Integer getTickOffset(final Integer clock) {
	if (!MachineRenderer.tickOffsets.containsKey(clock)) {
	    MachineRenderer.tickOffsets.put(clock, Event
		    .getMinExpectedStartTime(clock));
	}
	return MachineRenderer.tickOffsets.get(clock);
    }

    /**
     * 
     */
    public MachineRenderer(final Machine m, final Integer clock) {
	this.m = m;
	this.clock = clock;
    }

    private BufferedImage actuallyDraw() {
	final BufferedImage img = new BufferedImage(MachineRenderer.LINE_WIDTH,
		this.m.getCPUs() * MachineRenderer.NUM_PIXELS_PER_CPU,
		BufferedImage.TYPE_INT_RGB);
	final Graphics2D g = (Graphics2D) img.getGraphics();
	this.fineTuneGraphics(g);
	boolean isActive = Machine.isActive(this.m, this.clock);
	if (isActive) {
	    g.setColor(Color.WHITE);
	} else {
	    g.setColor(Color.DARK_GRAY);
	}
	g.fill3DRect(0, 0, img.getWidth() - 1, img.getHeight() - 1, true);
	this.drawJobs(img);
	g.setFont(MachineRenderer.font);
	if (isActive) {
	    g.setColor(Color.BLACK);
	    g.drawString(this.m.getName(), 2, 10);
	} else {
	    g.setColor(Color.WHITE);
	    g.drawString(this.m.getName() + " (off-line)", 2, 10);
	}
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
		f = File.createTempFile("schedvis" + MachineRenderer.instanceId
			+ "-t" + this.clock + "m" + this.m.getId() + ".",
			".gif");
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
	this.fineTuneGraphics(g);
	// render jobs in a schedule, one by one
	for (final Event evt : Machine.getLatestSchedule(this.m, this.clock)) {
	    // get assigned CPUs, set will ensure they are unique and sorted
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
		final int crntCPU = cpus[i];
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
		final int jobStartX = this.getStartingPosition(evt);
		final int jobLength = this.getJobLength(evt);
		final int ltY = crntCPU * MachineRenderer.NUM_PIXELS_PER_CPU;
		final int jobHgt = numCPUs * MachineRenderer.NUM_PIXELS_PER_CPU;
		g.setColor(this.getJobColor(evt.getJob()));
		g.fill3DRect(jobStartX, ltY, jobLength, jobHgt, true);
	    }
	}
    }

    private void fineTuneGraphics(final Graphics2D g) {
	g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_SPEED);
	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_OFF);
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
	    return Math.round((evt.getExpectedStart() - MachineRenderer
		    .getTickOffset(this.clock))
		    * MachineRenderer.NUM_PIXELS_PER_TICK);
	} catch (final NullPointerException e) {
	    return 0;
	}
    }

}
