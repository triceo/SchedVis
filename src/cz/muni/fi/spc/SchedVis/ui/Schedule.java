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
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.JobHint;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Job;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.util.Benchmark;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * This class knows how to render schedule for a machine into an image.
 * Performance of whole application essentially boils down to performance of
 * this class and entity methods it calls.
 * 
 * For benchmark mode of the application, it contains some special debugging
 * code that allows for precise timing of all its operations - that can be used
 * to measure and later further optimize the performance of this critical class.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Schedule implements Runnable {
	private static final class Colors {

		/**
		 * Colors that are available for the jobs. This array can be extended at
		 * will and the color-picking code will adjust to it.
		 * 
		 * Please remember not to use following colors: white (background for
		 * machines), dark gray (background for disabled machines) and red (overdue
		 * jobs).
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
	 * A class representing a holder for schedule data. Put into a thread pool to
	 * fetch database data in background and use getWhatever() methods to retrieve
	 * the fetched data.
	 */
	private static final class Data implements Runnable {

		private final Machine m;
		private final Event e;

		private boolean isActive = true;
		private List<Job> jobs = null;
		private final CountDownLatch activityLatch = new CountDownLatch(1);
		private final CountDownLatch scheduleLatch = new CountDownLatch(1);

		private boolean done = false;

		public Data(final Machine m, final Event e) {
			this.m = m;
			this.e = e;
		}

		public Event getEvent() {
			return this.e;
		}

		public Machine getMachine() {
			return this.m;
		}

		public List<Job> getSchedule() {
			try {
				this.scheduleLatch.await();
			} catch (final InterruptedException e) {
				return this.getSchedule();
			}
			return this.jobs;
		}

		public boolean isMachineActive() {
			try {
				this.activityLatch.await();
			} catch (final InterruptedException e) {
				return this.isMachineActive();
			}
			return this.isActive;
		}

		@Override
		public void run() {
			if (this.done) {
				return;
			}
			final Integer uuid = Benchmark.startProfile("schedule",
			    this.getMachine(), this.getEvent());
			this.jobs = Machine.getLatestSchedule(this.m, this.e);
			Benchmark.stopProfile(uuid);
			this.scheduleLatch.countDown();
			final Integer uuid2 = Benchmark.startProfile("activity", this
			    .getMachine(), this.getEvent());
			this.isActive = Machine.isActive(this.m, this.e);
			Benchmark.stopProfile(uuid2);
			this.activityLatch.countDown();
			this.done = true;
		}

	}

	/**
	 * The executor for fetching schedule data. It always creates at least one
	 * thread, at most two less than the number of available processors.
	 */
	private static final ExecutorService e = Executors.newFixedThreadPool(Math
	    .max(Runtime.getRuntime().availableProcessors() - 2, 1));

	/**
	 * How many pixels shall one CPU of a machine occupy on the y axis of the
	 * schedule.
	 */
	public static final int NUM_PIXELS_PER_CPU = Configuration
	    .getNumberOfPixelsPerCPU();
	/**
	 * How many pixels shall be used per a single tick on the x axis of the
	 * schedule.
	 */
	private static final float NUM_PIXELS_PER_TICK = Configuration
	    .getMaxImageWidth()
	    / (float) Job.getMaxSpan();
	public final int IMAGE_HEIGHT;
	/**
	 * How many pixels should be left in the left of the schedule for jobs that
	 * were supposed to be executed before the current clock.
	 */
	private static final int OVERFLOW_WIDTH = Double.valueOf(
	    Math.floor((Job.getMaxSpan() * Schedule.NUM_PIXELS_PER_TICK) / 8))
	    .intValue();
	/**
	 * Total length of the x axis of the schedule. If you need to change it,
	 * please change the input values, possibly in the config file, and not the
	 * equation.
	 */
	public final static int IMAGE_WIDTH = Double.valueOf(
	    Math.floor((Job.getMaxSpan() * Schedule.NUM_PIXELS_PER_TICK)
	        + Schedule.OVERFLOW_WIDTH)).intValue();

	private static final int CPU_OCCUPATION_MARK_WIDTH = Schedule.OVERFLOW_WIDTH / 5;

	/**
	 * Holds a font used throughout the schedules.
	 */
	private static final Font font = new Font("Monospaced", Font.PLAIN, 9);

	private static final Logger logger = Logger.getLogger(Schedule.class);

	/**
	 * How many ticks per a guiding bar should there be on the schedule.
	 */
	private static final int TICKS_PER_GUIDING_BAR = Configuration
	    .getNumberOfTicksPerGuide();

	private static final BasicStroke thinStroke = new BasicStroke(1);

	private static final BasicStroke thickStroke = new BasicStroke(2);

	private static Map<String, Paint> paints = new ConcurrentHashMap<String, Paint>();

	private static Rectangle textureRectangle = new Rectangle(0, 0,
	    Schedule.NUM_PIXELS_PER_CPU, Schedule.NUM_PIXELS_PER_CPU);

	private static final int BAR_DISTANCE = Math.max(1, Math
	    .round(Schedule.TICKS_PER_GUIDING_BAR * Schedule.NUM_PIXELS_PER_TICK));

	private static final int NUM_BARS = Schedule.IMAGE_WIDTH
	    / Schedule.BAR_DISTANCE;

	/**
	 * Parse the CPUs that have been assigned to a given job.
	 * 
	 * @param schedule
	 *          The job in question.
	 * @return Numbers of assigned CPUs.
	 */
	private static Set<Integer> getAssignedCPUs(final Job schedule) {
		// get assigned CPUs
		final String raw = schedule.getAssignedCPUs();
		if ((raw == null) || (raw.length() == 0)) {
			return new TreeSet<Integer>();
		}
		// parse single numbers
		final String[] rawParts = raw.split(",");
		// store for returning
		final Set<Integer> assignedCPUs = new TreeSet<Integer>();
		for (final String num : rawParts) {
			assignedCPUs.add(Integer.valueOf(num));
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
	private static int getJobLength(final Job schedule) {
		final Integer end = schedule.getExpectedEnd();
		final Integer start = schedule.getExpectedStart();
		if ((end == null) || (start == null)) {
			return 0;
		}
		return (int) ((end - start) * Schedule.NUM_PIXELS_PER_TICK);
	}

	/**
	 * Get the starting position for the event, when being rendered on the screen.
	 * 
	 * @param schedule
	 *          The event in question.
	 * @return The X starting coordinate.
	 */
	private static int getStartingPosition(final Job schedule, final int clock) {
		final Integer start = schedule.getExpectedStart();
		if (start == null) {
			return Schedule.OVERFLOW_WIDTH;
		}
		return (int) (((start - clock) * Schedule.NUM_PIXELS_PER_TICK) + Schedule.OVERFLOW_WIDTH);
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
	private static Paint getTexture(final Color background, final JobHint jobHint) {
		if ((jobHint == null) || (jobHint == JobHint.NONE)) {
			return background;
		}
		final String textureId = background.toString() + "---" + jobHint;
		if (!Schedule.paints.containsKey(textureId)) {
			final BufferedImage texture = GraphicsEnvironment
			    .getLocalGraphicsEnvironment().getDefaultScreenDevice()
			    .getDefaultConfiguration().createCompatibleImage(
			        Schedule.NUM_PIXELS_PER_CPU, Schedule.NUM_PIXELS_PER_CPU,
			        Transparency.OPAQUE);
			final Graphics2D g = (Graphics2D) texture.getGraphics();
			g.setBackground(background);
			g.clearRect(0, 0, texture.getWidth(), texture.getHeight());
			g.setColor(Color.WHITE);
			if (jobHint == JobHint.ARRIVAL) {
				// arrival; a plus is drawn
				final int mid = (texture.getHeight() / 2);
				g.drawLine(1, mid, texture.getWidth() - 2, mid);
				g.drawLine(mid, 1, mid, texture.getHeight() - 2);
			} else if (jobHint == JobHint.MOVE_NOK) {
				// bad move; a cross is drawn
				g.drawLine(1, 1, texture.getWidth() - 2, texture.getHeight() - 2);
				g.drawLine(1, texture.getHeight() - 2, texture.getWidth() - 2, 1);
			} else {
				// a good move; a tick is drawn
				g.drawLine(1, 1, 1, texture.getHeight() - 2);
				g.drawLine(1, texture.getHeight() - 2, texture.getWidth() - 2, 1);
			}
			Schedule.paints.put(textureId, new TexturePaint(texture,
			    Schedule.textureRectangle));
		}
		return Schedule.paints.get(textureId);
	}

	private Graphics2D g;
	private final Data data;

	/**
	 * Class constructor.
	 * 
	 * @param m
	 *          Machine to render.
	 * @param evt
	 *          A point in time in which we want the schedule rendered.
	 */
	public Schedule(final Machine m, final Event evt) {
		this.data = new Data(m, evt);
		Schedule.e.execute(this.data);
		this.IMAGE_HEIGHT = m.getCPUs() * Schedule.NUM_PIXELS_PER_CPU;
	}

	/**
	 * Takes machine schedule data and renders them. Please note that this method
	 * produces unclear job boundaries, probably because of rounding.
	 * 
	 * @param g
	 *          The graphics in question.
	 */
	private void drawJobs(final Graphics2D g) {
		for (final Job job : this.data.getSchedule()) {
			final Integer[] cpus = Schedule.getAssignedCPUs(job).toArray(
			    new Integer[] {});
			if (job.getBringsSchedule()) { // render jobs in a schedule, one by one
				for (int i = 0; i < cpus.length; i++) {
					final int crntCPU = cpus[i];
					// isolate a contiguous block of CPUs
					while (true) {
						if ((i + 1) == cpus.length) {
							break;
						}
						if (cpus[i + 1] - cpus[i] > 1) {
							break;
						}
						i++;
					}
					final int numCPUs = cpus[i] - crntCPU + 1;
					// now draw
					final int jobStartX = Schedule.getStartingPosition(job, this.data
					    .getEvent().getClock());
					if (jobStartX < 0) { // could be bad, may not be
						Schedule.logger.info(new Formatter().format(Messages
						    .getString("ScheduleRenderer.22"), new Object[] {
						    this.data.getMachine().getName(), this.data.getEvent().getId(),
						    jobStartX }));
					}
					final int jobLength = Schedule.getJobLength(job);
					final int ltY = crntCPU * Schedule.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * Schedule.NUM_PIXELS_PER_CPU;
					final int deadline = job.getDeadline();
					if ((deadline > -1) && (deadline < this.data.getEvent().getClock())) {
						g.setColor(Color.RED); // the job has a deadline and has missed it
					} else {
						g.setColor(Colors.getJobColor(job.getNumber())); // no deadline
					}
					final Shape s = new Rectangle(jobStartX, ltY, jobLength, jobHgt);
					g.setPaint(Schedule.getTexture(g.getColor(), job.getHint()));
					g.fill(s);
					g.setColor(Color.BLACK);
					if (this.data.getEvent().getId() == job.getNumber()) {
						g.setStroke(Schedule.thickStroke);
					} else {
						g.setStroke(Schedule.thinStroke);
					}
					g.draw(s);
					g.drawString(String.valueOf(job.getNumber()), Math.max(jobStartX + 2,
					    2), ltY + jobHgt - 2);
					final int rightBoundary = jobStartX + jobLength
					    - Schedule.IMAGE_WIDTH;
					if (rightBoundary > 0) {
						Schedule.logger.warn(new Formatter().format(Messages
						    .getString("ScheduleRenderer.23"), new Object[] {
						    this.data.getMachine().getName(), this.data.getEvent().getId(),
						    rightBoundary }));
					}
				}
			} else { // render CPU occupation
				if (cpus.length == 0) {
					continue;
				}
				for (int i = 0; i < cpus.length; i++) {
					final int crntCPU = cpus[i];
					// isolate a contiguous block of CPUs
					while (true) {
						if ((i + 1) == cpus.length) {
							break;
						}
						if (cpus[i + 1] - cpus[i] > 1) {
							break;
						}
						i++;
					}
					final int numCPUs = cpus[i] - crntCPU + 1;
					// now draw
					final int ltY = crntCPU * Schedule.NUM_PIXELS_PER_CPU;
					final int jobHgt = numCPUs * Schedule.NUM_PIXELS_PER_CPU;
					g.setColor(Color.RED);
					final Shape s = new Rectangle(0, ltY,
					    Schedule.CPU_OCCUPATION_MARK_WIDTH, jobHgt);
					g.fill(s);
					g.setColor(Color.BLACK);
					g.setStroke(Schedule.thinStroke);
					g.draw(s);
				}
			}
		}
	}

	/**
	 * Get the background for the schedule.
	 * 
	 * @return The background image.
	 */
	private void drawTemplate(final Graphics2D g) {
		g.setColor(Color.LIGHT_GRAY);
		// draw lines separating CPUs
		for (int cpu = 1; cpu < this.data.getMachine().getCPUs(); cpu += 2) {
			// two lines at a time, reducing number of loops
			final int yAxis = cpu * Schedule.NUM_PIXELS_PER_CPU;
			g.drawRect(-1, yAxis, Schedule.IMAGE_WIDTH + 1, yAxis
			    + Schedule.NUM_PIXELS_PER_CPU);
		}
		// draw bars showing position on the timeline
		for (int bar = 1; bar <= Schedule.NUM_BARS; bar += 2) {
			// two lines at a time, reducing number of loops
			final int xAxis = Schedule.BAR_DISTANCE * bar;
			g.drawRect(xAxis, -1, xAxis + Schedule.BAR_DISTANCE,
			    this.IMAGE_HEIGHT + 1);
		}
		// draw a line in a place where "zero" (current clock) is.
		g.setColor(Color.BLACK);
		g.drawLine(Schedule.OVERFLOW_WIDTH, 0, Schedule.OVERFLOW_WIDTH,
		    this.IMAGE_HEIGHT);
	}

	/**
	 * @return the g
	 */
	private Graphics2D getGraphics() {
		if (this.g == null) {
			throw new IllegalStateException("No graphics supplied for a schedule.");
		}
		return this.g;
	}

	@Override
	public void run() {
		final Integer globalUuid = Benchmark.startProfile("total", this.data
		    .getMachine(), this.data.getEvent());
		Integer uuid = Benchmark.startProfile("template");
		this.drawTemplate(this.getGraphics());
		Benchmark.stopProfile(uuid);

		uuid = Benchmark.startProfile("rendering");
		this.drawJobs(this.getGraphics());
		// add machine info
		final StringBuilder b = new StringBuilder().append(
		    this.data.getMachine().getName()).append("@").append(
		    this.data.getEvent().getClock());
		if (!this.data.isMachineActive()) {
			b.append(Messages.getString("ScheduleRenderer.19"));
		}
		this.getGraphics().setColor(Color.BLACK);
		this.getGraphics().drawString(b.toString(), 1, 9);
		Benchmark.stopProfile(uuid);
		Benchmark.stopProfile(globalUuid);
	}

	public void setTargetGraphics(final Graphics2D g) {
		this.g = g;
		g.setFont(Schedule.font);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		    RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		    RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		    RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_DITHERING,
		    RenderingHints.VALUE_DITHER_DISABLE);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
		    RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}
