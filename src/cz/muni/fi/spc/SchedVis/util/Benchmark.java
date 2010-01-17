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
		private final Event e;
		private final Machine m;

		public Intermediate(final String id, final Event e, final Machine m) {
			this.id = id;
			this.e = e;
			this.m = m;
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

	private static final Map<String, List<Long>> timesByType = Collections
	    .synchronizedMap(new HashMap<String, List<Long>>());
	private static final Map<String, List<Long>> timesByMachine = Collections
	    .synchronizedMap(new HashMap<String, List<Long>>());

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
		Benchmark.timesByType.clear();
	}

	/**
	 * Compute average value of many values.
	 * 
	 * @param values
	 *          The values.
	 * @return The average.
	 */
	protected static Double getAverage(final List<Long> values) {
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
	protected static Long getMedian(final Long[] sortedVals) {
		return Benchmark.getNthQuartil(sortedVals, 2);
	}

	protected static Long getNthQuartil(final Long[] sortedVals, final Integer n) {
		final Integer numVals = sortedVals.length;
		if (numVals % 4 == 1) {
			return sortedVals[((numVals / 4) * n) + 1];
		} else if ((numVals % 2 == 1) && (n == 2)) {
			return sortedVals[(numVals / 2) + 1];
		}
		final Double lowerBound = Math.floor((numVals / 4) * n);
		final Double upperBound = Math.ceil((numVals / 4) * n);
		return (sortedVals[lowerBound.intValue()] + sortedVals[upperBound
		    .intValue()]) / 2;
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
		String type = i.getId();
		String machineName = i.getMachine().getName();
		// log by type
		if (!Benchmark.timesByType.containsKey(type)) {
			Benchmark.timesByType.put(type, new ArrayList<Long>());
		}
		Benchmark.timesByType.get(type).add(time);
		// log by machine
		if (!Benchmark.timesByMachine.containsKey(machineName)) {
			Benchmark.timesByMachine.put(machineName, new ArrayList<Long>());
		}
		Benchmark.timesByMachine.get(machineName).add(time);
	}

	private static double nanoToMilli(final double nano) {
		return nano / 1000 / 1000;
	}

	private static double nanoToMilli(final long nano) {
		return Benchmark.nanoToMilli((double) nano);
	}

	protected static void reportLogResults() {
		Benchmark.reportLogResults(Benchmark.timesByType);
		System.out.println();
		Benchmark.reportLogResults(Benchmark.timesByMachine);
	}

	/**
	 * Output a table with results of the performance benchmark.
	 */
	private static void reportLogResults(final Map<String, List<Long>> times) {
		// show globals
		System.out
		    .println(" task \\ time [ms] |    avg    |    min    |    1/4    |    mid    |    3/4    |    max    "); //$NON-NLS-1$
		System.out
		    .println(" ------------------------------------------------------------------------------------------"); //$NON-NLS-1$
		for (final Entry<String, List<Long>> entry : times.entrySet()) {
			List<Long> allValues = entry.getValue();
			// sort the list
			final Long[] allValuesSorted = allValues.toArray(new Long[] {});
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
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(allValuesSorted[0])) + " | " //$NON-NLS-1$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(Benchmark.getNthQuartil(allValuesSorted, 1))) + " | " //$NON-NLS-1$ $NON-NLS-2$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(Benchmark.getMedian(allValuesSorted))) + " | " //$NON-NLS-1$ $NON-NLS-2$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.nanoToMilli(Benchmark.getNthQuartil(allValuesSorted, 3))) + " | " //$NON-NLS-1$ $NON-NLS-2$
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
		final Integer BENCH_EVERY_NTH = Configuration.getBenchmarkFrequency();
		final Integer NUMBER_OF_BENCHES = Configuration.getBenchmarkIterations();
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
		final BufferedImage img = new BufferedImage(Schedule.IMAGE_WIDTH,
		    Schedule.NUM_PIXELS_PER_CPU * m.getCPUs(),
		    BufferedImage.TYPE_BYTE_BINARY, Benchmark.model);
		final Graphics2D g = img.createGraphics();

		SwingUtilities.invokeAndWait(new Schedule(m, e, g));
	}

	public static UUID startProfile(final String id, final Event e,
	    final Machine m) {
		if (!Benchmark.isEnabled) {
			return null;
		}
		final UUID uuid = UUID.randomUUID();
		final Intermediate i = new Intermediate(id, e, m);
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
