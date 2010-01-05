package cz.muni.fi.spc.SchedVis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

public final class Benchmark {

	private static final Map<String, Map<Machine, List<Double>>> logTimes = new HashMap<String, Map<Machine, List<Double>>>();

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
	 * Log a time it took to execute a given task.
	 * 
	 * @param type
	 *          ID of a task that was being executed.
	 * @param m
	 *          The machine on which it was executed.
	 * @param time
	 *          The time it took.
	 */
	public static void logTime(final String type, final Machine m,
	    final double time) {
		if (!Benchmark.logTimes.containsKey(type)) {
			Benchmark.logTimes.put(type, new HashMap<Machine, List<Double>>());
		}
		final Map<Machine, List<Double>> logMachine = Benchmark.logTimes.get(type);
		if (!logMachine.containsKey(m)) {
			logMachine.put(m, new ArrayList<Double>());
		}
		final List<Double> machineTimes = logMachine.get(m);
		machineTimes.add(time);
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
		for (final Entry<String, Map<Machine, List<Double>>> entry : Benchmark.logTimes
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
			System.out
			    .println("  " //$NON-NLS-1$
			        + new PrintfFormat("%15s").sprintf(entry.getKey()) //$NON-NLS-1$
			        + " | " //$NON-NLS-1$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.getAverage(allValues) * 1000) + " | " //$NON-NLS-1$ $$NON-NLS-2$
			        + new PrintfFormat(" %.5f ").sprintf(allValuesSorted[0] * 1000) //$NON-NLS-1$
			        + " | " //$NON-NLS-1$
			        + new PrintfFormat(" %.5f ").sprintf(Benchmark.getMedian(allValuesSorted) * 1000) + " | " //$NON-NLS-1$ $NON-NLS-2$
			        + new PrintfFormat(" %.5f ") //$NON-NLS-1$
			            .sprintf(allValuesSorted[allValuesSorted.length - 1] * 1000));
		}
	}

	/**
	 * Runs some basic benchmarks. Basically renders some random schedules many,
	 * many, many times and outputs the resulting time.
	 */
	public synchronized static void run() {
		final Integer BENCH_EVERY_NTH = 500;
		System.out.println(Messages.getString("Main.0")); //$NON-NLS-1$
		System.out.println();
		// run!
		final Set<Machine> machines = Machine.getAllGroupless();
		final Set<Integer> ticks = Event.getAllTicks();
		Integer i = 0;
		for (final int tick : Event.getAllTicks()) {
			if (tick % BENCH_EVERY_NTH != 0) {
				continue;
			}
			i++;
			for (final Machine m : machines) {
				ScheduleRenderingController.getRendered(m, Event.getWithId(tick));
			}
			System.out.println(new PrintfFormat(Messages.getString("Main.1")) //$NON-NLS-1$
			    .sprintf(new Integer[] { i, ticks.size() / BENCH_EVERY_NTH, tick }));
		}
		ScheduleRenderingController.restart(); // wait until all is done
		System.out.println();
		System.out.println(Messages.getString("Main.4")); //$NON-NLS-1$
		Benchmark.reportLogResults();
		return;
	}

	private Benchmark() {
		// prevent instantialization
	}

}
