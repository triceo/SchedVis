package cz.muni.fi.spc.SchedVis.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.Schedule;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

public final class Benchmark {

	private final static class Intermediate {

		private long startTime;
		private final String id;
		private final Machine m;

		public Intermediate(final String id, final Machine m) {
			this.id = id;
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

	private static final Map<String, Map<Machine, List<Long>>> logTimes = Collections
	    .synchronizedMap(new HashMap<String, Map<Machine, List<Long>>>());

	private static boolean isEnabled = false;

	private static Map<UUID, Intermediate> inters = Collections
	    .synchronizedMap(new HashMap<UUID, Intermediate>());

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
		Benchmark.logTimes.clear();
	}

	/**
	 * Compute average value of many values.
	 * 
	 * @param values
	 *          The values.
	 * @return The average.
	 */
	private static Double getAverage(final List<Long> values) {
		double total = 0.0;
		for (final Long value : values) {
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
	private static Long getMedian(final Long[] sortedVals) {
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
	    final long time) {
		if (!Benchmark.logTimes.containsKey(type)) {
			Benchmark.logTimes.put(type, new HashMap<Machine, List<Long>>());
		}
		final Map<Machine, List<Long>> logMachine = Benchmark.logTimes.get(type);
		if (!logMachine.containsKey(m)) {
			logMachine.put(m, new ArrayList<Long>());
		}
		final List<Long> machineTimes = logMachine.get(m);
		machineTimes.add(time);
	}

	private static double nanoToMilli(final double nano) {
		return nano / 1000 / 1000;
	}

	private static double nanoToMilli(final long nano) {
		return Benchmark.nanoToMilli((double) nano);
	}

	/**
	 * Output a table with results of the performance benchmark.
	 */
	private static void reportLogResults() {
		// show globals
		System.out
		    .println(" task \\ time [ms] |    avg    |    min    |    mid    |    max    "); //$NON-NLS-1$
		System.out
		    .println(" -----------------------------------------------------------------"); //$NON-NLS-1$
		for (final Entry<String, Map<Machine, List<Long>>> entry : Benchmark.logTimes
		    .entrySet()) {
			List<Long> allValues = new ArrayList<Long>();
			for (final Entry<Machine, List<Long>> perMachine : entry.getValue()
			    .entrySet()) {
				allValues.addAll(perMachine.getValue());
			}
			// sort the list
			Long[] allValuesSorted = allValues.toArray(new Long[] {});
			Arrays.sort(allValuesSorted);
			allValues = Arrays.asList(allValuesSorted);
			// remove upper and lower ${extremesPercent} % of values (the
			// extremes)
			final int extremesPercent = 2;
			final Double extremeValueCount = (new Double(allValues.size()) / 100.0)
			    * extremesPercent;
			allValues = allValues.subList(extremeValueCount.intValue(), allValues
			    .size());
			allValues = allValues.subList(0, allValues.size()
			    - extremeValueCount.intValue());
			// tabulate results
			System.out
			    .println("  " //$NON-NLS-1$
			        + new PrintfFormat("%15s").sprintf(entry.getKey()) //$NON-NLS-1$
			        + " | " //$NON-NLS-1$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(Benchmark.getAverage(allValues))) + " | " //$NON-NLS-1$ $$NON-NLS-2$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(allValuesSorted[0])) //$NON-NLS-1$
			        + " | " //$NON-NLS-1$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(Benchmark.getMedian(allValuesSorted))) + " | " //$NON-NLS-1$ $NON-NLS-2$
			        + new PrintfFormat(" %.5f ") //$NON-NLS-1$
			            .sprintf(Benchmark
			                .nanoToMilli(allValuesSorted[allValuesSorted.length - 1])));
		}
	}

	/**
	 * Runs some basic benchmarks. Basically renders some random schedules many,
	 * many, many times and outputs the resulting time.
	 */
	public static void run() throws Exception {
		if (Benchmark.isEnabled) {
			throw new Exception("Benchmark already running.");
		}
		Benchmark.isEnabled = true;
		final Integer BENCH_EVERY_NTH = 500;
		final Integer NUMBER_OF_BENCHES = 10;
		System.out.println(Messages.getString("Main.0")); //$NON-NLS-1$
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
				System.out.println(new PrintfFormat(Messages.getString("Main.1")) //$NON-NLS-1$
				    .sprintf(new Integer[] { i,
				        (ticks.size() / BENCH_EVERY_NTH) * NUMBER_OF_BENCHES, tick }));
			}
		}
		System.out.println();
		System.out.println(Messages.getString("Main.4")); //$NON-NLS-1$
		Benchmark.reportLogResults();
		Benchmark.isEnabled = false;
		return;
	}

	public static void runSingleSchedule(final Event e, final Machine m)
	    throws Exception {
		BufferedImage img = new BufferedImage(Schedule.IMAGE_WIDTH,
		    Schedule.NUM_PIXELS_PER_CPU * m.getCPUs(),
		    BufferedImage.TYPE_BYTE_BINARY, Benchmark.model);
		final Graphics2D g = img.createGraphics();

		SwingUtilities.invokeAndWait(new Schedule(m, e, g));
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
		Benchmark.logTime(i.getId(), i.getMachine(), difference);
		return difference;
	}

	private Benchmark() {
		// prevent instantialization
	}
}
