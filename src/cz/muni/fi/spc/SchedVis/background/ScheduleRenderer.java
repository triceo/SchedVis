/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.background;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.PrintfFormat;

/**
 * This class knows how to render schedule for a machine into an image.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleRenderer extends SwingWorker<Image, Void> {

	/**
	 * Holds the machine whose schedule is currently being rendered.
	 */
	private final Machine m;
	/**
	 * Holds the position on the timeline that is currently being rendered.
	 */
	private final int virtualClock;
	/**
	 * How many pixels shall one CPU of a machine occupy on the y axis of the
	 * schedule.
	 */
	private static final Integer NUM_PIXELS_PER_CPU = Configuration
	    .getNumberOfPixelsPerCPU();
	/**
	 * How many pixels shall be used per a single tick on the x axis of the
	 * schedule.
	 */
	private static final Float NUM_PIXELS_PER_TICK = Configuration
	    .getMaxImageWidth()
	    / (float) Event.getMaxJobSpan();
	/**
	 * How many pixels should be left in the left of the schedule for jobs that
	 * were supposed to be executed before the current clock.
	 */
	private static final Integer OVERFLOW_WIDTH = Double
	    .valueOf(
	        Math
	            .floor((Event.getMaxJobSpan() * ScheduleRenderer.NUM_PIXELS_PER_TICK) / 8))
	    .intValue();
	/**
	 * Total length of the x axis of the schedule. If you need to change it,
	 * please change the input values, not the equation.
	 */
	private static final Integer LINE_WIDTH = Double.valueOf(
	    Math.floor((Event.getMaxJobSpan() * ScheduleRenderer.NUM_PIXELS_PER_TICK)
	        + ScheduleRenderer.OVERFLOW_WIDTH)).intValue();
	/**
	 * Colors that are available for the jobs. This array can be extended at will
	 * and the color-picking code will adjust to it.
	 * 
	 * Please remember not to use following colors: white (background for
	 * machines), dark gray (background for disabled machines) and red (overdue
	 * jobs).
	 */
	private static final Color[] colors = { Color.BLUE, Color.CYAN, Color.GREEN,
	    Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.PINK,
	    Color.YELLOW };

	/**
	 * Holds a font used throughout the schedules. Memory use improvement.
	 */
	private static final Font font = new Font("Monospaced", Font.PLAIN, 9);

	/**
	 * Holds events in a currently rendered schedule. Stored for performance
	 * reasons.
	 */
	private List<Event> events;

	private static final Logger logger = Logger.getLogger(ScheduleRenderer.class);

	/**
	 * Micro-optimization. Holds the parsed values of assigned CPUs.
	 */
	private static final Map<String, Integer[]> sets = new HashMap<String, Integer[]>();

	/**
	 * Holds colors for different jobs, so that they persist and are the same over
	 * the whole application runtime.
	 */
	private static final Map<Integer, Color> jobsToColors = new HashMap<Integer, Color>();
	/**
	 * Random for assigning job colors.
	 */
	private static final Random rand = new Random();

	/**
	 * How many ticks per a guiding bar should there be on the schedule.
	 */
	private static final Integer TICKS_PER_GUIDING_BAR = Configuration
	    .getNumberOfTicksPerGuide();

	/**
	 * The current point in time that we are rendering.
	 */
	private int clock;

	/**
	 * Class constructor.
	 * 
	 * @param m
	 *          Machine to render.
	 * @param clock
	 *          A point in time in which we want the schedule rendered.
	 */
	public ScheduleRenderer(final Machine m, final Integer clock) {
		this.m = m;
		this.virtualClock = clock;
	}

	/**
	 * Performs the actual drawing of the machine schedule. Draws a frame and
	 * calls another method to perform drawing of jobs.
	 * 
	 * @return The rendered image.
	 */
	@Override
	public Image doInBackground() {
		final Double globalTime = Double.valueOf(System.nanoTime());
		this.clock = this.getClock();
		Double time = globalTime;
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting clock. Took %.5f seconds.")
		    .sprintf(time));

		time = Double.valueOf(System.nanoTime());
		this.events = Machine.getLatestSchedule(this.m, this.virtualClock);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting schedule. Took %.5f seconds.")
		    .sprintf(time));

		time = Double.valueOf(System.nanoTime());
		final boolean isActive = Machine.isActive(this.m, this.virtualClock);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting activity. Took %.5f seconds.")
		    .sprintf(time));

		time = Double.valueOf(System.nanoTime());
		final BufferedImage img = this.getTemplate(isActive);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting template. Took %.5f seconds.")
		    .sprintf(time));

		time = Double.valueOf(System.nanoTime());
		final Graphics2D g = (Graphics2D) img.getGraphics();
		this.fineTuneGraphics(g);
		g.setFont(ScheduleRenderer.font);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting graphics. Took %.5f seconds.")
		    .sprintf(time));

		time = Double.valueOf(System.nanoTime());
		this.drawJobs(g);
		// add machine info
		if (isActive) {
			g.setColor(Color.BLACK);
			g.drawString(this.m.getName() + "@" + this.clock, 1, 9);
		} else {
			g.setColor(Color.WHITE);
			g.drawString(this.m.getName() + "@" + this.clock + " (off-line)", 1, 9);
		}
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished rendering. Took %.5f seconds.")
		    .sprintf(time));

		time = (System.nanoTime() - globalTime) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished. Took %.5f seconds.").sprintf(time));
		return img;
	}

	/**
	 * Takes machine schedule data and renders them.
	 * 
	 * @param g
	 *          The graphics in question.
	 * @todo Produces unclear job boundaries, probably because of rounding.
	 */
	private void drawJobs(final Graphics2D g) {
		Double time = Double.valueOf(System.nanoTime());
		for (final Event evt : this.events) {
			if (evt.getBringsSchedule()) {
				// render jobs in a schedule, one by one
				/*
				 * isolate all the contiguous blocks of CPUs in the job and paint them.
				 */
				final Integer[] cpus = this.getAssignedCPUs(evt);
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
						// might be ok, but might also be bad. so inform.
						ScheduleRenderer.logger.info("Machine " + this.m.getName() + " at "
						    + this.virtualClock + " is drawing " + jobStartX
						    + " before its boundary.");
					}
					final int jobLength = this.getJobLength(evt);
					final int ltY = crntCPU * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					if ((evt.getDeadline() > -1) && (evt.getDeadline() < this.clock)) {
						// the job has a deadline and has missed it
						g.setColor(Color.RED);
					} else {
						// job with no deadlines
						g.setColor(this.getJobColor(evt.getJob()));
					}
					final Shape s = new Rectangle(jobStartX, ltY, jobLength, jobHgt);
					g.setPaint(this.getTexture(g.getColor(), evt.getJobHint()));
					g.fill(s);
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(1));
					g.draw(s);
					g.drawString(evt.getJob().toString(), Math.max(jobStartX + 2, 2), ltY
					    + jobHgt - 2);
					final int rightBoundary = jobStartX + jobLength
					    - ScheduleRenderer.LINE_WIDTH;
					if (rightBoundary > 0) {
						// always bad. warn.
						ScheduleRenderer.logger.warn("Machine " + this.m.getName() + " at "
						    + this.virtualClock + " is drawing " + rightBoundary
						    + " over its boundary.");
					}
				}
			} else {
				// render CPU occupation
				final Integer[] cpus = this.getAssignedCPUs(evt);
				if (cpus.length == 0) {
					continue;
				}
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
					final int jobStartX = 0;
					final int jobLength = ScheduleRenderer.OVERFLOW_WIDTH / 5;
					final int ltY = crntCPU * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					g.setColor(Color.RED);
					final Shape s = new Rectangle(jobStartX, ltY, jobLength, jobHgt);
					g.fill(s);
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(1));
					g.draw(s);
				}
			}
		}
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished drawing jobs. Took %.5f seconds.")
		    .sprintf(time));
	}

	/**
	 * Tweaks the graphics object so that it performs better. Probably just a
	 * micro-optimization.
	 * 
	 * @param g
	 *          The graphics in question.
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
	 * Parse the CPUs that have been assigned to a given job.
	 * 
	 * @param evt
	 *          The job in question.
	 * @return Numbers of assigned CPUs.
	 */
	private Integer[] getAssignedCPUs(final Event evt) {
		// get assigned CPUs, set will ensure they are unique and sorted
		if ((evt.getAssignedCPUs() == null)
		    || (evt.getAssignedCPUs().trim().length() == 0)) {
			return new Integer[] {};
		}
		synchronized (ScheduleRenderer.sets) {
			if (!ScheduleRenderer.sets.containsKey(evt.getAssignedCPUs())) {
				final Set<Integer> assignedCPUs = new TreeSet<Integer>();
				for (final String num : evt.getAssignedCPUs().split(",")) {
					assignedCPUs.add(Integer.valueOf(num));
				}
				ScheduleRenderer.sets.put(evt.getAssignedCPUs(), assignedCPUs
				    .toArray(new Integer[0]));
			}
		}
		return ScheduleRenderer.sets.get(evt.getAssignedCPUs());
	}

	/**
	 * Get the actual clock that is assigned to the virtual clock.
	 * 
	 * @return The clock.
	 */
	private Integer getClock() {
		return Event.getLastWithVirtualClock(this.virtualClock);
	}

	/**
	 * Make sure a job has always the same color, no matter when and where it is
	 * painted.
	 * 
	 * @param jobId
	 * @return A color that shall be used for that job. May be ignored when we
	 *         need to use another color for a job, indicating some special state
	 *         the job is in.
	 */
	private synchronized Color getJobColor(final Integer jobId) {
		if (!ScheduleRenderer.jobsToColors.containsKey(jobId.intValue())) {
			final Integer random = ScheduleRenderer.rand
			    .nextInt(ScheduleRenderer.colors.length);
			ScheduleRenderer.jobsToColors.put(jobId.intValue(),
			    ScheduleRenderer.colors[random]);
		}
		return ScheduleRenderer.jobsToColors.get(jobId.intValue());
	}

	/**
	 * Calculate the length of a job on the screen.
	 * 
	 * @param evt
	 *          The job in question.
	 * @return Length in pixels.
	 */
	private int getJobLength(final Event evt) {
		final Integer end = evt.getExpectedEnd();
		final Integer start = evt.getExpectedStart();
		if ((end == null) || (start == null)) {
			return 0;
		}
		return Double.valueOf(
		    Math.floor((end - start) * ScheduleRenderer.NUM_PIXELS_PER_TICK))
		    .intValue();
	}

	/**
	 * Get the starting position for the event, when being rendered on the screen.
	 * 
	 * @param evt
	 *          The event in question.
	 * @return The X starting coordinate.
	 */
	private int getStartingPosition(final Event evt) {
		final Integer start = evt.getExpectedStart();
		if (start == null) {
			return ScheduleRenderer.OVERFLOW_WIDTH;
		}
		return Double.valueOf(
		    Math.floor((start - this.clock) * ScheduleRenderer.NUM_PIXELS_PER_TICK)
		        + ScheduleRenderer.OVERFLOW_WIDTH).intValue();
	}

	/**
	 * Get the background for the schedule.
	 * 
	 * @param isActive
	 *          Whether the background should indicate an active machine.
	 * @return The background image.
	 */
	private BufferedImage getTemplate(final boolean isActive) {
		Double time = Double.valueOf(System.nanoTime());
		final int numCPUs = this.m.getCPUs().intValue();
		final BufferedImage img = new BufferedImage(ScheduleRenderer.LINE_WIDTH,
		    numCPUs * ScheduleRenderer.NUM_PIXELS_PER_CPU,
		    BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = (Graphics2D) img.getGraphics();
		this.fineTuneGraphics(g);
		// draw background
		if (isActive) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(Color.DARK_GRAY);
		}
		g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
		// draw the grid
		if (isActive) {
			g.setColor(Color.LIGHT_GRAY);
		} else {
			g.setColor(Color.GRAY);
		}
		for (Integer cpu = 0; cpu < (this.m.getCPUs() - 1); cpu++) {
			g.drawLine(0, (cpu + 1) * ScheduleRenderer.NUM_PIXELS_PER_CPU,
			    ScheduleRenderer.LINE_WIDTH - 2, (cpu + 1)
			        * ScheduleRenderer.NUM_PIXELS_PER_CPU);
		}
		final Integer barDistance = Math.max(1, Math
		    .round(ScheduleRenderer.TICKS_PER_GUIDING_BAR
		        * ScheduleRenderer.NUM_PIXELS_PER_TICK));
		for (Integer bar = 0; bar < (ScheduleRenderer.LINE_WIDTH / barDistance); bar++) {
			g.drawLine(barDistance * (bar + 1), 0, barDistance * (bar + 1), img
			    .getHeight() - 2);
		}
		g.setColor(Color.black);
		// draw a line in a place where "zero" (current clock) is.
		g.drawLine(ScheduleRenderer.OVERFLOW_WIDTH, 0,
		    ScheduleRenderer.OVERFLOW_WIDTH, numCPUs
		        * ScheduleRenderer.NUM_PIXELS_PER_CPU);

		// finish
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logger.debug(new PrintfFormat(this.m.getName() + "@"
		    + this.virtualClock + " finished getting template. Took %.5f seconds.")
		    .sprintf(time));
		return img;
	}

	/**
	 * Get texture for the event's box.
	 * 
	 * @param background
	 *          The color of the event box's background.
	 * @param jobHint
	 *          The type of event actually rendered.
	 * @return The texture.
	 */
	private Paint getTexture(final Color background, final Integer jobHint) {
		if ((jobHint == null) || jobHint.equals(Event.JOB_HINT_NONE)) {
			return background;
		}
		final BufferedImage texture = new BufferedImage(
		    ScheduleRenderer.NUM_PIXELS_PER_CPU,
		    ScheduleRenderer.NUM_PIXELS_PER_CPU, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = (Graphics2D) texture.getGraphics();
		g.setColor(background);
		g.fill(new Rectangle(0, 0, ScheduleRenderer.NUM_PIXELS_PER_CPU,
		    ScheduleRenderer.NUM_PIXELS_PER_CPU));
		g.setColor(Color.WHITE);
		if (jobHint.equals(Event.JOB_HINT_ARRIVAL)) {
			// arrival; a plus is drawn
			final Integer mid = (ScheduleRenderer.NUM_PIXELS_PER_CPU / 2);
			g.drawLine(1, mid, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2, mid);
			g.drawLine(mid, 1, mid, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2);
		} else if (jobHint.equals(Event.JOB_HINT_MOVE_NOK)) {
			// bad move; a cross is drawn
			g.drawLine(1, 1, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2,
			    ScheduleRenderer.NUM_PIXELS_PER_CPU - 2);
			g.drawLine(1, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2,
			    ScheduleRenderer.NUM_PIXELS_PER_CPU - 2, 1);
		} else {
			// a good move; a tick is drawn
			g.drawLine(1, 1, 1, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2);
			g.drawLine(1, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2,
			    ScheduleRenderer.NUM_PIXELS_PER_CPU - 2, 1);
		}
		return new TexturePaint(texture, new Rectangle(0, 0,
		    ScheduleRenderer.NUM_PIXELS_PER_CPU,
		    ScheduleRenderer.NUM_PIXELS_PER_CPU));
	}

}
