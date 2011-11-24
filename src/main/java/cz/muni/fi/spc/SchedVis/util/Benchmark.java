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
package cz.muni.fi.spc.SchedVis.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.Schedule;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * A class that provides benchmarking facilities for SchedVis.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Benchmark {

	/**
	 * This class servers as a pool for BufferedImages that are used for
	 * benchmarking purposes. By generating these couple of instances, we can
	 * eliminate a big deal of work during the benchmark, leading to more
	 * consistent results.
	 * 
	 * Start as a background thread and then access images using the get() method.
	 */
	private static final class ImagePool implements Runnable {

		private final Queue<BufferedImage> q = new ConcurrentLinkedQueue<BufferedImage>();
		private final Integer numberOfMachines;
		private final Integer numberOfItems;
		private final CountDownLatch l = new CountDownLatch(1);

		public ImagePool(final Integer machines) {
			this.numberOfMachines = machines;
			this.numberOfItems = machines * 4;
		}

		public BufferedImage get() {
			try {
				this.l.await();
			} catch (final InterruptedException ex) {
				return this.get();
			}
			final BufferedImage i = this.q.poll();
			i.flush();
			this.q.add(i);
			return i;
		}

		public void run() {
			final GraphicsConfiguration g = GraphicsEnvironment
			    .getLocalGraphicsEnvironment().getDefaultScreenDevice()
			    .getDefaultConfiguration();
			for (int i = 0; i < this.numberOfItems; i++) {
				this.q.add(g.createCompatibleImage(Schedule.IMAGE_WIDTH,
				    Schedule.NUM_PIXELS_PER_CPU * 40, Transparency.TRANSLUCENT));
				if (i == (this.numberOfItems % this.numberOfMachines)) {
					this.l.countDown();
				}
			}
		}

	}

	/**
	 * A class that holds intermediate measuring results that will be disposed
	 * of after the given measurement is taken.
	 */
	private final static class Intermediate {

		private long startTime;
		private final String id;
		private Machine m;
		private Event e;

		public Intermediate(final String id) {
			this.id = id;
			this.m = null;
			this.e = null;
		}

		public Intermediate(final String id, final Machine m, final Event e) {
			this(id);
			this.m = m;
			this.e = e;
		}

		public Event getEvent() {
			return this.e;
		}

		public String getId() {
			return this.id;
		}

		public Machine getMachine() {
			return this.m;
		}

		public long getStartTime() {
			return this.startTime;
		}

		public void setStartTime(final long nanotime) {
			this.startTime = nanotime;
		}

	}

	private static final ImagePool mic = new ImagePool(Machine.getAllGroupless()
	    .size());

	private static final ConcurrentMap<String, List<Long>> timesByType = new ConcurrentSkipListMap<String, List<Long>>();
	private static final ConcurrentMap<String, List<Long>> timesByMachine = new ConcurrentSkipListMap<String, List<Long>>();

	private static final ConcurrentMap<String, List<Long>> timesByEvent = new ConcurrentSkipListMap<String, List<Long>>();

	private static boolean isEnabled = false;

	private static ConcurrentMap<Integer, Intermediate> inters = new ConcurrentHashMap<Integer, Intermediate>();

	private static AtomicInteger uuidGenerator = new AtomicInteger(0);

	public static void clearLogResults() {
		Benchmark.timesByType.clear();
		Benchmark.timesByEvent.clear();
		Benchmark.timesByMachine.clear();
	}

	/**
	 * Log a time it took to execute a given task.
	 * 
	 * @param i
	 *          The intermediate data holder.
	 * @param time
	 *          The time it took.
	 */
	protected static void logTime(final Intermediate i, final long time) {
		final String type = i.getId();
		// log by type
		Benchmark.timesByType.putIfAbsent(type, Collections
		    .synchronizedList(new ArrayList<Long>()));
		Benchmark.timesByType.get(type).add(time);
		// log by machine
		if (i.getMachine() != null) {
			final String machineName = i.getMachine().getName();
			Benchmark.timesByMachine.putIfAbsent(machineName, Collections
			    .synchronizedList(new ArrayList<Long>()));
			Benchmark.timesByMachine.get(machineName).add(time);
		}
		// log by event
		if (i.getEvent() != null) {
			final String eventName = Integer.toString(i.getEvent().getId());
			Benchmark.timesByEvent.putIfAbsent(eventName, Collections
			    .synchronizedList(new ArrayList<Long>()));
			Benchmark.timesByEvent.get(eventName).add(time);
		}
	}

	private static double nanoToMilli(final double nano) {
		return nano / 1000 / 1000;
	}

	protected static void reportLogResults() {
		System.out.println("Benchmark by machine:");
		Benchmark.reportLogResults(Benchmark.timesByMachine);
		// System.out.println();
		// System.out.println("Benchmark by event:");
		// Benchmark.reportLogResults(Benchmark.timesByEvent);
		System.out.println();
		System.out.println("Benchmark by type of event:");
		Benchmark.reportLogResults(Benchmark.timesByType);
	}

	/**
	 * Output a table with results of the performance benchmark.
	 */
	private static void reportLogResults(final Map<String, List<Long>> times) {
		// show globals
		System.out
		    .println(" task \\ time [ms] |    avg    |    1/50   |    1/4    |    1/2    |    3/4    |   49/50    | std. dev.");
		System.out
		    .println(" ------------------------------------------------------------------------------------------------------");
		for (final Entry<String, List<Long>> entry : times.entrySet()) {
			final DescriptiveStatistics stats = new DescriptiveStatistics();
			for (final double v : entry.getValue()) {
				stats.addValue(v);
			}
			// tabulate results
			System.out
			    .println(new Formatter()
			        .format(
			            "  %15s |  %.5f  |  %.5f  |  %.5f  |  %.5f  |  %.5f  |  %.5f   |  %.5f",
			            entry.getKey(), Benchmark.nanoToMilli(stats.getMean()),
			            Benchmark.nanoToMilli(stats.getPercentile(2)), Benchmark
			                .nanoToMilli(stats.getPercentile(25)), Benchmark
			                .nanoToMilli(stats.getPercentile(50)), Benchmark
			                .nanoToMilli(stats.getPercentile(75)), Benchmark
			                .nanoToMilli(stats.getPercentile(98)), Benchmark
			                .nanoToMilli(stats.getStandardDeviation())));
		}
		System.out.println("Schedules processed: "
		    + Benchmark.timesByType.get("total").size());
	}

	/**
	 * Runs some basic benchmarks. Basically renders some random schedules many,
	 * many, many times and outputs the resulting time.
	 */
	public static void run() throws Exception {
		if (Benchmark.isEnabled) {
			throw new IllegalStateException("Benchmark already running.");
		}
		Benchmark.isEnabled = true;
		System.out.println("Please press any key to start benchmark...");
		try {
			System.in.read();
		} catch (final IOException ex) {
			// nothing can be done. ignore.
		}
		new Thread(Benchmark.mic, "ImageGeneratorThread").start();
		final Integer BENCH_EVERY_NTH = Configuration.getBenchmarkFrequency();
		final Integer NUMBER_OF_BENCHES = Configuration.getBenchmarkIterations();
		System.out.println(Messages.getString("Main.0"));
		System.out.println();
		// run!
		final Set<Machine> machines = Machine.getAllGroupless();
		final Set<Event> ticks = new HashSet<Event>();
		for (final Integer eventId : Event.getAllTicks()) {
			if ((eventId % BENCH_EVERY_NTH) != 0) {
				continue;
			}
			ticks.add(Event.getWithId(eventId));
		}
		Integer i = 0;
		Long time = System.nanoTime();
		for (int count = 0; count < NUMBER_OF_BENCHES; count++) {
			for (final Event tick : ticks) {
				i++;
				for (final Machine m : machines) {
					Benchmark.runSingleSchedule(tick, m, false);
				}
				System.out.println(new Formatter().format(Messages.getString("Main.1"),
				    new Object[] { i, ticks.size(), tick.getId() }));
			}
		}
		System.out.println();
		System.out.println(Messages.getString("Main.4"));
		Benchmark.reportLogResults();
		time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
		System.out.println("Total time: " + time + " seconds.");
		Benchmark.isEnabled = false;
		return;
	}

	public static void runSingleSchedule(final Event e, final Machine m,
	    final boolean useOwnImages) throws Exception {
		BufferedImage img = null;
		if (useOwnImages) {
			img = GraphicsEnvironment.getLocalGraphicsEnvironment()
			    .getDefaultScreenDevice().getDefaultConfiguration()
			    .createCompatibleImage(Schedule.IMAGE_WIDTH,
			        Schedule.NUM_PIXELS_PER_CPU * m.getCPUs());
		} else {
			img = Benchmark.mic.get();
		}
		final Graphics2D g = img.createGraphics();
		final Schedule s = new Schedule(m, e);
		s.setTargetGraphics(g);
		/*
		 * Randomly decide whether to invoke now or later. This should be the best
		 * emulation of what happens in the Swing GUI.
		 */
		if (Math.round((Math.random() * 10) % 2) == 0) {
			SwingUtilities.invokeAndWait(s);
		} else {
			SwingUtilities.invokeLater(s);
		}
	}

	public static Integer startProfile(final String id) {
		if (!Benchmark.isEnabled) {
			return null;
		}
		final Intermediate i = new Intermediate(id);
		final Integer uuid = Benchmark.uuidGenerator.incrementAndGet();
		Benchmark.inters.put(uuid, i);
		i.setStartTime(System.nanoTime());
		return uuid;
	}

	public static Integer startProfile(final String id, final Machine m,
	    final Event e) {
		if (!Benchmark.isEnabled) {
			return null;
		}
		final Intermediate i = new Intermediate(id, m, e);
		final Integer uuid = Benchmark.uuidGenerator.incrementAndGet();
		Benchmark.inters.put(uuid, i);
		i.setStartTime(System.nanoTime());
		return uuid;
	}

	public static long stopProfile(final Integer uuid) {
		if (!Benchmark.isEnabled) {
			return 0;
		}
		final long now = System.nanoTime();
		try {
			final Intermediate i = Benchmark.inters.remove(uuid);
			final long difference = now - i.getStartTime();
			Benchmark.logTime(i, difference);
			return difference;
		} catch (final NullPointerException ex) {
			/*
			 * FIXME: This is synchronization problem when removing from the map.
			 * However, no root cause has yet been found.
			 */
			return -1;
		}
	}

	private Benchmark() {
		// prevent instantialization
	}
}
