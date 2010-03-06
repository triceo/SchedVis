package cz.muni.fi.spc.SchedVis.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

		public Intermediate(final String id) {
			this.id = id;
			this.m = null;
		}

		public Intermediate(final String id, final Machine m) {
			this(id);
			this.m = m;
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

	private static final ConcurrentMap<String, List<Long>> timesByType = new ConcurrentHashMap<String, List<Long>>();
	private static final ConcurrentMap<String, List<Long>> timesByMachine = new ConcurrentHashMap<String, List<Long>>();

	private static boolean isEnabled = false;

	private static ConcurrentMap<UUID, Intermediate> inters = new ConcurrentHashMap<UUID, Intermediate>();

	/**
	 * Index color model specifying 16 basic colors. This significantly improves
	 * the speed of preparing the images, thus decreasing the time the benchmark
	 * will take.
	 */
	public static final IndexColorModel model = new IndexColorModel(4, 16,
	    new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 192,
	        (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 0, (byte) 0,
	        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0 }, new byte[] {
	        (byte) 255, (byte) 255, (byte) 0, (byte) 0, (byte) 192, (byte) 128,
	        (byte) 128, (byte) 0, (byte) 0, (byte) 255, (byte) 255, (byte) 128,
	        (byte) 128, (byte) 0, (byte) 0, (byte) 0 }, new byte[] { (byte) 255,
	        (byte) 0, (byte) 255, (byte) 0, (byte) 192, (byte) 128, (byte) 0,
	        (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0,
	        (byte) 255, (byte) 128, (byte) 0 });

	public static void clearLogResults() {
		Benchmark.timesByType.clear();
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
		Benchmark.timesByType.putIfAbsent(type, new ArrayList<Long>());
		Benchmark.timesByType.get(type).add(time);
		// log by machine
		if (i.getMachine() != null) {
			final String machineName = i.getMachine().getName();
			Benchmark.timesByMachine.putIfAbsent(machineName, new ArrayList<Long>());
			Benchmark.timesByMachine.get(machineName).add(
			    time / i.getMachine().getCPUs());
		}
	}

	private static double nanoToMilli(final double nano) {
		return nano / 1000 / 1000;
	}

	protected static void reportLogResults() {
		System.out.println("Benchmark by machine (normalized by number of CPUs):");
		Benchmark.reportLogResults(Benchmark.timesByMachine);
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
		    .println(" task \\ time [ms] |    avg    |    min    |    1/4    |    mid    |    3/4    |    max    | std. dev.");
		System.out
		    .println(" ------------------------------------------------------------------------------------------");
		for (final Entry<String, List<Long>> entry : times.entrySet()) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (double v : entry.getValue()) {
				stats.addValue(v);
			}
			// tabulate results
			System.out
			    .println(new Formatter()
			        .format(
			            "  %15s |  %.5f  |  %.5f  |  %.5f  |  %.5f  |  %.5f  |  %.5f  |  %.5f",
			            entry.getKey(), Benchmark.nanoToMilli(stats.getMean()),
			            Benchmark.nanoToMilli(stats.getMin()), Benchmark
			                .nanoToMilli(stats.getPercentile(25)), Benchmark
			                .nanoToMilli(stats.getPercentile(50)), Benchmark
			                .nanoToMilli(stats.getPercentile(75)), Benchmark
			                .nanoToMilli(stats.getMax()), Benchmark.nanoToMilli(stats
			                .getStandardDeviation())));
		}
	}

	/**
	 * Runs some basic benchmarks. Basically renders some random schedules many,
	 * many, many times and outputs the resulting time.
	 */
	public static void run() {
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

	public static void runSingleSchedule(final Event e, final Machine m) {
		final BufferedImage img = new BufferedImage(Schedule.IMAGE_WIDTH,
		    Schedule.NUM_PIXELS_PER_CPU * m.getCPUs(),
		    BufferedImage.TYPE_BYTE_BINARY, Benchmark.model);
		final Graphics2D g = img.createGraphics();
		try {
			final Schedule s = new Schedule(m, e);
			s.setTargetGraphics(g);
			s.run();
		} catch (final Exception ex) {
			System.out.println("Thread caught exception: " + ex);
		}
	}

	public static UUID startProfile(final String id) {
		if (!Benchmark.isEnabled) {
			return null;
		}
		final UUID uuid = UUID.randomUUID();
		final Intermediate i = new Intermediate(id);
		Benchmark.inters.put(uuid, i);
		i.setStartTime(System.nanoTime());
		return uuid;
	}

	public static UUID startProfile(final String id, final Machine m) {
		if (!Benchmark.isEnabled) {
			return null;
		}
		final UUID uuid = UUID.randomUUID();
		final Intermediate i = new Intermediate(id, m);
		Benchmark.inters.put(uuid, i);
		i.setStartTime(System.nanoTime());
		return uuid;
	}

	public static long stopProfile(final UUID uuid) {
		if (!Benchmark.isEnabled) {
			return 0;
		}
		final long now = System.nanoTime();
		final Intermediate i = Benchmark.inters.remove(uuid);
		final long difference = now - i.getStartTime();
		Benchmark.logTime(i, difference);
		return difference;
	}

	private Benchmark() {
		// prevent instantialization
	}
}
