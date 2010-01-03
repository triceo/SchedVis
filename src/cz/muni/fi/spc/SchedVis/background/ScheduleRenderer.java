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
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Job;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.Messages;
import cz.muni.fi.spc.SchedVis.util.PrintfFormat;

/**
 * This class knows how to render schedule for a machine into an image.
 * Performance of whole application essentially boils down to berformance of
 * this class and entity methods it calls.
 * 
 * For benchmark mode of the application, it contains some special debugging
 * code that allows for precise timing of all its operations - that can be used
 * to measure and later further optimize the performance of this critical class.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleRenderer extends SwingWorker<Image, Void> {

	private static final class Colors {
		/**
		 * Index color model specifying 16 basic colors. This significantly improves
		 * the speed of rendering the images.
		 */
		public static final IndexColorModel model = new IndexColorModel(4, 16,
		    new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255,
		        (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
		        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
		        (byte) 0 }, new byte[] { (byte) 255, (byte) 255, (byte) 0,
		        (byte) 0, (byte) 192, (byte) 128, (byte) 128, (byte) 0, (byte) 0,
		        (byte) 255, (byte) 255, (byte) 128, (byte) 128, (byte) 0, (byte) 0,
		        (byte) 0 }, new byte[] { (byte) 255, (byte) 0, (byte) 255,
		        (byte) 0, (byte) 192, (byte) 128, (byte) 0, (byte) 128, (byte) 0,
		        (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 128,
		        (byte) 0 });

		/**
		 * Colors that are available for the jobs. This array can be extended at
		 * will and the color-picking code will adjust to it.
		 * 
		 * Please remember not to use following colors: white (background for
		 * machines), dark gray (background for disabled machines) and red (overdue
		 * jobs).
		 * 
		 * Also, if you increase the amount of colors available, please also change
		 * the associated color model and mind its associated comments.
		 */
		private static final Color[] colors = { Color.BLUE, Color.CYAN,
		    Color.GREEN, Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY,
		    Color.PINK, Color.YELLOW };

		/**
		 * Assign a color for the job. This picks a "random" color for a job and
		 * keeps it all through the program execution.
		 * 
		 * @param jobId
		 *          ID of the job.
		 * @return The job color.
		 */
		public static Color getJobColor(final Integer jobId) {
			return Colors.colors[jobId % Colors.colors.length];
		}

	}

	/**
	 * Holds the machine whose schedule is currently being rendered.
	 */
	private final Machine m;
	/**
	 * Holds the position on the timeline that is currently being rendered.
	 */
	private final Event renderedEvent;
	/**
	 * How many pixels shall one CPU of a machine occupy on the y axis of the
	 * schedule.
	 */
	private static final int NUM_PIXELS_PER_CPU = Configuration
	    .getNumberOfPixelsPerCPU();
	/**
	 * How many pixels shall be used per a single tick on the x axis of the
	 * schedule.
	 */
	private static final float NUM_PIXELS_PER_TICK = Configuration
	    .getMaxImageWidth()
	    / (float) Job.getMaxSpan();
	/**
	 * How many pixels should be left in the left of the schedule for jobs that
	 * were supposed to be executed before the current clock.
	 */
	private static final int OVERFLOW_WIDTH = Double
	    .valueOf(
	        Math
	            .floor((Job.getMaxSpan() * ScheduleRenderer.NUM_PIXELS_PER_TICK) / 8))
	    .intValue();
	/**
	 * Total length of the x axis of the schedule. If you need to change it,
	 * please change the input values, possibly in the config file, and not the
	 * equation.
	 */
	private static final int LINE_WIDTH = Double.valueOf(
	    Math.floor((Job.getMaxSpan() * ScheduleRenderer.NUM_PIXELS_PER_TICK)
	        + ScheduleRenderer.OVERFLOW_WIDTH)).intValue();

	private static final int CPU_OCCUPATION_MARK_WIDTH = ScheduleRenderer.OVERFLOW_WIDTH / 5;

	/**
	 * Holds a font used throughout the schedules.
	 */
	private static final Font font = new Font("Monospaced", Font.PLAIN, 9); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(ScheduleRenderer.class);

	/**
	 * How many ticks per a guiding bar should there be on the schedule.
	 */
	private static final int TICKS_PER_GUIDING_BAR = Configuration
	    .getNumberOfTicksPerGuide();
	/**
	 * Stores debugging information.
	 */
	private static final Map<String, Map<Machine, List<Double>>> logTimes = new HashMap<String, Map<Machine, List<Double>>>();

	private static final BasicStroke thinStroke = new BasicStroke(1);

	private static final BasicStroke thickStroke = new BasicStroke(2);

	private static Map<String, Paint> paints = Collections
	    .synchronizedMap(new HashMap<String, Paint>());

	private static Rectangle textureRectangle = new Rectangle(0, 0,
	    ScheduleRenderer.NUM_PIXELS_PER_CPU, ScheduleRenderer.NUM_PIXELS_PER_CPU);

	private static final int BAR_DISTANCE = Math.max(1, Math
	    .round(ScheduleRenderer.TICKS_PER_GUIDING_BAR
	        * ScheduleRenderer.NUM_PIXELS_PER_TICK));

	public static void clearLogResults() {
		ScheduleRenderer.logTimes.clear();
	}

	/**
	 * Compute average value of many values.
	 * 
	 * @param values
	 *          The values.
	 * @return The average.
	 */
	private static Double getAverage(final List<Double> values) {
		double total = 0.0;
		for (final Double value : values) {
			total += value;
		}
		return total / values.size();
	}

	/**
	 * Compute the median of many values. A median is the number that splits the
	 * set of numbers into two sets of equal sizes.
	 * 
	 * @param sortedVals
	 *          The values, sorted.
	 * @return The median value.
	 */
	private static Double getMedian(final Double[] sortedVals) {
		final Integer numVals = sortedVals.length;
		if (numVals % 2 == 1) {
			return sortedVals[(numVals / 2) + 1];
		}
		final Double lowerBound = Math.floor(numVals / 2);
		final Double upperBound = Math.ceil(numVals / 2);
		return (sortedVals[lowerBound.intValue()] + sortedVals[upperBound
		    .intValue()]) / 2;
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
	private static Paint getTexture(final Color background, final Integer jobHint) {
		if ((jobHint == null) || jobHint.equals(Event.JOB_HINT_NONE)) {
			return background;
		}
		final String textureId = background.toString() + "---" + jobHint;
		if (!ScheduleRenderer.paints.containsKey(textureId)) {
			final int hint = jobHint.intValue();
			final BufferedImage texture = new BufferedImage(
			    ScheduleRenderer.NUM_PIXELS_PER_CPU,
			    ScheduleRenderer.NUM_PIXELS_PER_CPU, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = (Graphics2D) texture.getGraphics();
			g.setColor(background);
			g.fill(ScheduleRenderer.textureRectangle);
			g.setColor(Color.WHITE);
			if (hint == Event.JOB_HINT_ARRIVAL) {
				// arrival; a plus is drawn
				final int mid = (ScheduleRenderer.NUM_PIXELS_PER_CPU / 2);
				g.drawLine(1, mid, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2, mid);
				g.drawLine(mid, 1, mid, ScheduleRenderer.NUM_PIXELS_PER_CPU - 2);
			} else if (hint == Event.JOB_HINT_MOVE_NOK) {
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
			ScheduleRenderer.paints.put(textureId, new TexturePaint(texture,
			    ScheduleRenderer.textureRectangle));
		}
		return ScheduleRenderer.paints.get(textureId);
	}

	/**
	 * Log a time it took to execute a given task.
	 * 
	 * @param type
	 *          ID of a task that was being executed.
	 * @param m
	 *          The machine on which it was executed.
	 * @param time
	 *          The time it took.
	 */
	private static void logTime(final String type, final Machine m,
	    final double time) {
		if (!ScheduleRenderer.logTimes.containsKey(type)) {
			ScheduleRenderer.logTimes.put(type, new HashMap<Machine, List<Double>>());
		}
		final Map<Machine, List<Double>> logMachine = ScheduleRenderer.logTimes
		    .get(type);
		if (!logMachine.containsKey(m)) {
			logMachine.put(m, new ArrayList<Double>());
		}
		final List<Double> machineTimes = logMachine.get(m);
		machineTimes.add(time);
		ScheduleRenderer.logger.debug(new PrintfFormat(Messages
		    .getString("ScheduleRenderer.1")).sprintf(new Object[] { //$NON-NLS-1$
		    m.getName(), type, time }));
	}

	/**
	 * Output a table with results of the performance benchmark.
	 */
	public static void reportLogResults() {
		// show globals
		System.out
		    .println(" task \\ time [ms] |    avg    |    min    |    mid    |    max    "); //$NON-NLS-1$
		System.out
		    .println(" -----------------------------------------------------------------"); //$NON-NLS-1$
		for (final Entry<String, Map<Machine, List<Double>>> entry : ScheduleRenderer.logTimes
		    .entrySet()) {
			List<Double> allValues = new ArrayList<Double>();
			for (final Entry<Machine, List<Double>> perMachine : entry.getValue()
			    .entrySet()) {
				allValues.addAll(perMachine.getValue());
			}
			// sort the list
			Double[] allValuesSorted = allValues.toArray(new Double[] {});
			Arrays.sort(allValuesSorted);
			allValues = Arrays.asList(allValuesSorted);
			// remove upper and lower ${extremesPercent} % of values (the extremes)
			final int extremesPercent = 1;
			final Double extremeValueCount = (new Double(allValues.size()) / 100.0)
			    * extremesPercent;
			allValues = allValues.subList(extremeValueCount.intValue(), allValues
			    .size());
			allValues = allValues.subList(0, allValues.size()
			    - extremeValueCount.intValue());
			allValuesSorted = allValues.toArray(new Double[] {});
			// tabulate results
			System.out.println("  " //$NON-NLS-1$
			    + new PrintfFormat("%15s").sprintf(entry.getKey()) //$NON-NLS-1$
			    + " | " //$NON-NLS-1$
			    + new PrintfFormat(" %.5f ").sprintf(ScheduleRenderer //$NON-NLS-1$
			        .getAverage(allValues) * 1000) + " | " //$NON-NLS-1$
			    + new PrintfFormat(" %.5f ").sprintf(allValuesSorted[0] * 1000) //$NON-NLS-1$
			    + " | " //$NON-NLS-1$
			    + new PrintfFormat(" %.5f ").sprintf(ScheduleRenderer //$NON-NLS-1$
			        .getMedian(allValuesSorted) * 1000) + " | " //$NON-NLS-1$
			    + new PrintfFormat(" %.5f ") //$NON-NLS-1$
			        .sprintf(allValuesSorted[allValuesSorted.length - 1] * 1000));
		}
	}

	private BufferedImage img;

	/**
	 * Class constructor.
	 * 
	 * @param m
	 *          Machine to render.
	 * @param evt
	 *          A point in time in which we want the schedule rendered.
	 */
	public ScheduleRenderer(final Machine m, final Event evt) {
		this.m = m;
		this.renderedEvent = evt;
	}

	/**
	 * Performs the actual drawing of the machine schedule.
	 * 
	 * @return The rendered image.
	 */
	@Override
	public Image doInBackground() {
		final Double globalTime = Double.valueOf(System.nanoTime());
		Double time = globalTime;
		final boolean isActive = Machine.isActive(this.m, this.renderedEvent);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logTime("activity", this.m, time); //$NON-NLS-1$

		time = Double.valueOf(System.nanoTime());
		final Graphics2D g = this.getTemplate(isActive);
		g.setFont(ScheduleRenderer.font);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logTime("template", this.m, time); //$NON-NLS-1$

		time = Double.valueOf(System.nanoTime());
		final List<Job> jobs = Machine
		    .getLatestSchedule(this.m, this.renderedEvent);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logTime("schedule", this.m, time); //$NON-NLS-1$

		time = Double.valueOf(System.nanoTime());
		this.drawJobs(g, jobs);
		// add machine info
		String descriptor = this.m.getName() + "@" + this.renderedEvent.getClock(); //$NON-NLS-1$
		if (!isActive) {
			descriptor += Messages.getString("ScheduleRenderer.19"); //$NON-NLS-1$
		}
		g.setColor(isActive ? Color.BLACK : Color.WHITE);
		g.drawString(descriptor, 1, 9);
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		ScheduleRenderer.logTime("rendering", this.m, time); //$NON-NLS-1$

		time = (System.nanoTime() - globalTime) / 1000 / 1000 / 1000;
		ScheduleRenderer.logTime("total", this.m, time); //$NON-NLS-1$
		return this.img;
	}

	/**
	 * Takes machine schedule data and renders them. Please note that this method
	 * produces unclear job boundaries, probably because of rounding.
	 * 
	 * @param g
	 *          The graphics in question.
	 */
	private void drawJobs(final Graphics2D g, final List<Job> jobs) {
		for (final Job job : jobs) {
			final int[] cpus = this.getAssignedCPUs(job);
			if (job.getBringsSchedule()) { // render jobs in a schedule, one by one
				/*
				 * isolate all the contiguous blocks of CPUs in the job and paint them.
				 */
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
					final int numCPUs = cpus[i] - crntCPU + 1;
					// now draw
					final int jobStartX = this.getStartingPosition(job);
					if (jobStartX < 0) { // could be bad, may not be
						ScheduleRenderer.logger.info(new PrintfFormat(Messages
						    .getString("ScheduleRenderer.22")) //$NON-NLS-1$
						    .sprintf(new Object[] { this.m.getName(),
						        this.renderedEvent.getId(), jobStartX }));
					}
					final int jobLength = this.getJobLength(job);
					final int ltY = crntCPU * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					final int deadline = job.getDeadline();
					if ((deadline > -1) && (deadline < this.renderedEvent.getClock())) {
						g.setColor(Color.RED); // the job has a deadline and has missed it
					} else {
						g.setColor(Colors.getJobColor(job.getJob())); // no deadline
					}
					final Shape s = new Rectangle(jobStartX, ltY, jobLength, jobHgt);
					g.setPaint(ScheduleRenderer
					    .getTexture(g.getColor(), job.getJobHint()));
					g.fill(s);
					g.setColor(Color.BLACK);
					if (this.renderedEvent.getId() == job.getJob()) {
						g.setStroke(ScheduleRenderer.thickStroke);
					} else {
						g.setStroke(ScheduleRenderer.thinStroke);
					}
					g.draw(s);
					g.drawString(String.valueOf(job.getJob()),
					    Math.max(jobStartX + 2, 2), ltY + jobHgt - 2);
					final int rightBoundary = jobStartX + jobLength
					    - ScheduleRenderer.LINE_WIDTH;
					if (rightBoundary > 0) {
						ScheduleRenderer.logger.warn(new PrintfFormat(Messages
						    .getString("ScheduleRenderer.23")) //$NON-NLS-1$
						    .sprintf(new Object[] { this.m.getName(),
						        this.renderedEvent.getId(), rightBoundary }));
					}
				}
			} else { // render CPU occupation
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
					final int numCPUs = cpus[i] - crntCPU + 1;
					// now draw
					final int ltY = crntCPU * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * ScheduleRenderer.NUM_PIXELS_PER_CPU;
					g.setColor(Color.RED);
					final Shape s = new Rectangle(0, ltY,
					    ScheduleRenderer.CPU_OCCUPATION_MARK_WIDTH, jobHgt);
					g.fill(s);
					g.setColor(Color.BLACK);
					g.setStroke(ScheduleRenderer.thinStroke);
					g.draw(s);
				}
			}
		}
	}

	/**
	 * Parse the CPUs that have been assigned to a given job.
	 * 
	 * @param schedule
	 *          The job in question.
	 * @return Numbers of assigned CPUs.
	 */
	private int[] getAssignedCPUs(final Job schedule) {
		// get assigned CPUs
		final String raw = schedule.getAssignedCPUs();
		if ((raw == null) || (raw.length() == 0)) {
			return new int[] {};
		}
		// parse single numbers
		final String[] rawParts = raw.split(","); //$NON-NLS-1$
		// store for returning
		final int[] assignedCPUs = new int[rawParts.length];
		int i = 0;
		for (final String num : rawParts) {
			assignedCPUs[i] = Integer.valueOf(num);
			i++;
		}
		return assignedCPUs;
	}

	/**
	 * Calculate the length of a job on the screen.
	 * 
	 * @param schedule
	 *          The job in question.
	 * @return Length in pixels.
	 */
	private int getJobLength(final Job schedule) {
		final Integer end = schedule.getExpectedEnd();
		final Integer start = schedule.getExpectedStart();
		if ((end == null) || (start == null)) {
			return 0;
		}
		return (int) ((end - start) * ScheduleRenderer.NUM_PIXELS_PER_TICK);
	}

	/**
	 * Get the starting position for the event, when being rendered on the screen.
	 * 
	 * @param schedule
	 *          The event in question.
	 * @return The X starting coordinate.
	 */
	private int getStartingPosition(final Job schedule) {
		final Integer start = schedule.getExpectedStart();
		if (start == null) {
			return ScheduleRenderer.OVERFLOW_WIDTH;
		}
		return (int) (((start - this.renderedEvent.getClock()) * ScheduleRenderer.NUM_PIXELS_PER_TICK) + ScheduleRenderer.OVERFLOW_WIDTH);
	}

	/**
	 * Get the background for the schedule.
	 * 
	 * @param isActive
	 *          Whether the background should indicate an active machine.
	 * @return The background image.
	 */
	private Graphics2D getTemplate(final boolean isActive) {
		final int imageHeight = this.m.getCPUs()
		    * ScheduleRenderer.NUM_PIXELS_PER_CPU;
		final int imageWidth = ScheduleRenderer.LINE_WIDTH;
		this.img = new BufferedImage(imageWidth, imageHeight,
		    BufferedImage.TYPE_BYTE_BINARY, Colors.model);
		final Graphics2D g = this.img.createGraphics();
		// draw background
		if (isActive) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(Color.DARK_GRAY);
		}
		g.fillRect(0, 0, imageWidth - 1, imageHeight - 1);
		// draw the grid
		if (isActive) {
			g.setColor(Color.LIGHT_GRAY);
		} else {
			g.setColor(Color.GRAY);
		}
		for (int cpu = 0; cpu < (this.m.getCPUs() - 1); cpu++) {
			final int yAxis = (cpu + 1) * ScheduleRenderer.NUM_PIXELS_PER_CPU;
			g.drawLine(0, yAxis, imageHeight - 2, yAxis);
		}
		final int numBars = ScheduleRenderer.LINE_WIDTH
		    / ScheduleRenderer.BAR_DISTANCE;
		for (int bar = 0; bar < numBars; bar++) {
			final int xAxis = ScheduleRenderer.BAR_DISTANCE * (bar + 1);
			g.drawLine(xAxis, 0, xAxis, imageHeight - 2);
		}
		g.setColor(Color.BLACK);
		// draw a line in a place where "zero" (current clock) is.
		g.drawLine(ScheduleRenderer.OVERFLOW_WIDTH, 0,
		    ScheduleRenderer.OVERFLOW_WIDTH, imageHeight);
		return g;
	}
}
