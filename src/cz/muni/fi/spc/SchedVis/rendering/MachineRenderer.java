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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.Database;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MachineRenderer extends SwingWorker<Image, Void> {

    /**
     * Holds the machine whose schedule is currently being rendered.
     */
    private final Machine m;

    /**
     * Holds a name of the database so that the cached schedule images from
     * different databases don't interfere.
     */
    private static final String instanceId = new File(Database.getName())
    .getName();

    /**
     * Holds the position on the timeline that is currently being rendered.
     */
    private final Integer clock;

    /**
     * How many pixels shall one CPU of a machine occupy on the y axis of the
     * schedule.
     */
    private static final Integer NUM_PIXELS_PER_CPU = 5;

    /**
     * How many pixels shall be used per a single tick on the x axis of the
     * schedule.
     */
    private static final Float NUM_PIXELS_PER_TICK = new Float(0.1);
    /**
     * Total length of the x axis of the schedule. If you need to change it,
     * please change the input values, not the equation.
     */
    private static final Integer LINE_WIDTH = Math.round(Event.getMaxJobSpan()
	    * MachineRenderer.NUM_PIXELS_PER_TICK);
    /**
     * Minimum length in pixels of a job in the schedule.
     */
    private static final Integer MIN_JOB_LENGTH_PIXELS = 4;

    /**
     * Colors that are available for the jobs. This array can be extended at
     * will and the color-picking code will adjust to it.
     * 
     * Please remember not to use following colors: white (background for
     * machines), dark gray (background for disabled machines) and red (overdue
     * jobs).
     */
    private static final Color[] colors = { Color.BLUE, Color.CYAN,
	Color.GREEN, Color.GRAY, Color.MAGENTA, Color.ORANGE,
	Color.LIGHT_GRAY, Color.PINK, Color.YELLOW };

    /**
     * Holds job colors so that the same jobs have always the same color,
     * regardless of schedule position or group placement.
     */
    private static final Map<Integer, Color> jobColors = new HashMap<Integer, Color>();

    /**
     * Holds a font used throughout the schedules. Memory use improvement.
     */
    private static Font font = new Font("Monospaced", Font.PLAIN, 9);

    /**
     * Holds the schedule for the current machine and the current position in
     * time.
     */
    private final List<Event> events;

    public MachineRenderer(final Machine m, final Integer clock) {
	this.m = m;
	this.events = Machine.getLatestSchedule(this.m, clock);
	this.clock = clock;
    }

    /**
     * Performs the actual drawing of the machine schedule. Draws a frame and
     * calls another method to perform drawing of jobs.
     * 
     * @return
     */
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

    /**
     * Background task to render the images used for machine schedules.
     * 
     * The logic in this method tries to cache rendered images whenever
     * possible. If a cache file is not found, image is rendered and, if
     * possible, cached so that it needs not be rendered next time.
     */
    @Override
    public Image doInBackground() {
	File f = new File("tmp/schedvis-" + MachineRenderer.instanceId + "-"
		+ this.clock + "-" + this.m.getId() + ".gif");
	if (!f.exists()) {
	    BufferedImage img = this.actuallyDraw();
	    try {
		ImageIO.write(img, "gif", f);
	    } catch (IOException e) {
		Logger.getLogger(MachineRenderer.class).warn("Won't cache machine " + this.m.getId() + " at " + this.clock + ". Failed to write into a file " + f.getAbsolutePath() + ".");
	    }
	    return img;
	} else {
	    try {
		return ImageIO.read(f);
	    } catch (IOException e) {
		Logger.getLogger(MachineRenderer.class).warn(
			"Cannot read cache for machine " + this.m.getId()
			+ " at " + this.clock
			+ ". Failed to write into a file "
			+ f.getAbsolutePath() + ".");
		return this.actuallyDraw();
	    }
	}
    }

    /**
     * Takes machine schedule data and renders them.
     * 
     * @param img
     * @todo Produces unclear job boundaries, probably because of rounding.
     */
    private void drawJobs(final Image img) {
	final Graphics2D g = (Graphics2D) img.getGraphics();
	this.fineTuneGraphics(g);
	// render jobs in a schedule, one by one
	for (final Event evt : this.events) {
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
		if (jobStartX < 0) {
		    Logger.getLogger(this.getClass()).warn(
			    "Machine " + this.m.getName() + " at " + this.clock
			    + " is drawing " + jobStartX
			    + " before its boundary.");
		}
		final int jobLength = this.getJobLength(evt);
		final int ltY = crntCPU * MachineRenderer.NUM_PIXELS_PER_CPU;
		final int jobHgt = numCPUs * MachineRenderer.NUM_PIXELS_PER_CPU;
		if ((evt.getDeadline() > -1)
			&& (evt.getDeadline() < this.clock)) {
		    // the job has a deadline and has missed it
		    g.setColor(Color.RED);
		} else {
		    // job with no deadlines
		    g.setColor(this.getJobColor(evt.getJob()));
		}
		g.fill3DRect(jobStartX, ltY, jobLength, jobHgt, true);
		g.setFont(MachineRenderer.font);
		g.setColor(Color.BLACK);
		g.drawString(evt.getJob().toString(), jobStartX + 2, ltY
			+ jobHgt - 2);
		int rightBoundary = jobStartX + jobLength
		- MachineRenderer.LINE_WIDTH;
		if (rightBoundary > 0) {
		    Logger.getLogger(this.getClass()).warn(
			    "Machine " + this.m.getName() + " at " + this.clock
			    + " is drawing " + rightBoundary
			    + "over its boundary.");
		}
	    }
	}
    }

    /**
     * Update the graphics object so that it performs better.
     * 
     * @param g
     */
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
     * @return A color that shall be used for that job. May be ignored when the
     *         job is overdue.
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
	    return Math.round((evt.getExpectedStart() - (this.events.get(0)
		    .getExpectedStart() - this.clock))
		    * MachineRenderer.NUM_PIXELS_PER_TICK);
	} catch (final NullPointerException e) {
	    return 0;
	}
    }

}
