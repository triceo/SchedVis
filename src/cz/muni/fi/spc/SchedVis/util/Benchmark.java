package cz.muni.fi.spc.SchedVis.util;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.Schedule;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

public final class Benchmark {

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
		final Integer BENCH_EVERY_NTH = Configuration.getBenchmarkFrequency();
		final Integer NUMBER_OF_BENCHES = Configuration.getBenchmarkIterations();
		System.out.println(Messages.getString("Main.0"));
		System.out.println();
		// run!
		final Set<Machine> machines = Machine.getAllGroupless();
		final Set<Integer> ticks = Event.getAllTicks();
		Integer i = 0;
		for (int count = 0; count < NUMBER_OF_BENCHES; count++) {
			for (final int tick : ticks) {
				if (tick % BENCH_EVERY_NTH != 0) {
					continue;
				}
				i++;
				for (final Machine m : machines) {
					Benchmark.runSingleSchedule(Event.getWithId(tick), m);
				}
				System.out.println(new Formatter().format(Messages.getString("Main.1"),
				    new Object[] { i,
				        (ticks.size() / BENCH_EVERY_NTH) * NUMBER_OF_BENCHES, tick }));
			}
		}
		System.out.println();
		System.out.println(Messages.getString("Main.4"));
		Benchmark.reportLogResults();
		Benchmark.isEnabled = false;
		return;
	}

	public static void runSingleSchedule(final Event e, final Machine m)
	    throws Exception {
		final BufferedImage img = GraphicsEnvironment
		    .getLocalGraphicsEnvironment()
		    .getDefaultScreenDevice()
		    .getDefaultConfiguration()
		    .createCompatibleImage(Schedule.IMAGE_WIDTH,
		        Schedule.NUM_PIXELS_PER_CPU * m.getCPUs(), Transparency.TRANSLUCENT);
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
		} catch (NullPointerException ex) {
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
