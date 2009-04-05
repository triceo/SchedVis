/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.Database;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.rendering.ScheduleRenderer;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main implements PropertyChangeListener {

	private static final Main main = new Main();

	private static MainFrame frame;

	/**
	 * Number of finished renderers. Used for calculating caching progress.
	 */
	private static Integer doneRenderers = 0;
	/**
	 * Total number of renderers available. Used for calculating caching progress.
	 */
	private static Integer totalRenderers = 0;
	/**
	 * Hash codes of renderers that are in the queue. Used for calculating caching
	 * progress.
	 */
	private static Set<Integer> queuedRenderers = new HashSet<Integer>();

	/**
	 * How many threads at most should be executing at the same time.
	 * 
	 * The coefficient has been determined by experiment to provide the best
	 * results. When threads block on IO operations, some other threads can be
	 * executed. Thus the number of threads exceeding the total number of cores is
	 * not a problem and provides a performance increase.
	 * 
	 * Should the program perform inefficiently in your use case, feel free to
	 * tweak this setting.
	 */
	private static final Integer MAX_RENDERER_THREADS = Configuration
	    .getNumberOfCPUCores() * 8;
	/**
	 * How many renderers should be ready to be executed when some other renderer
	 * finishes. If this number is set too low, it will be increased
	 * automatically.
	 */
	private static Integer MAX_QUEUED_RENDERERS = Main.MAX_RENDERER_THREADS * 2;

	/**
	 * Time in nanoseconds when we last reported progress of caching.
	 */
	private static Long lastReportTime;

	private static Logger logger = Logger.getLogger(Main.class);

	/**
	 * When the caching started. In nanoseconds.
	 */
	private static Double startProcessingTime;

	/**
	 * Stores few latest processing times. This is used to calculate the estimated
	 * time remaining to finish caching.
	 */
	private static Queue<Double> lastProcessingTimes = new LinkedList<Double>();

	/**
	 * When was the queue of renderers lenghtened last time. Lenghtening the queue
	 * prevents the thread pool from running under its limits.
	 */
	private static Long lastDoubledQueueLength = new Long(0);

	/**
	 * Estimate a remaining time that a job will take.
	 * 
	 * @param lastUnitTook
	 *          How long did last unit take. In nanoseconds.
	 * @param alreadyDonePct
	 *          How many units are already done. In percent.
	 * @param unitSize
	 *          How many renderable items does a unit contain.
	 * @param historySize
	 *          How many recent unit execution times should be taken into account.
	 * @return Remaining time estimation in nanoseconds.
	 */
	private static Double countProgress(final Double lastUnitTook,
	    final Double alreadyDonePct, final Integer unitSize,
	    final Integer historySize) {
		Main.lastProcessingTimes.add(lastUnitTook);
		if (Main.lastProcessingTimes.size() > historySize) {
			Main.lastProcessingTimes.remove();
		}
		Double totalTime = 0.0;
		for (Double m : Main.lastProcessingTimes) {
			totalTime += m;
		}
		Double averageTime = totalTime / Main.lastProcessingTimes.size();
		return ((1 - alreadyDonePct) * (Main.totalRenderers / unitSize))
		    * averageTime;
	}

	/**
	 * Get the main Swing frame. Useful for refreshing the whole GUI.
	 * 
	 * @return Main Swing UI frame.
	 */
	public static MainFrame getFrame() {
		return Main.frame;
	}

	/**
	 * Main method for the application. Checks input parameters, passes control
	 * to other parts of this class.
	 * 
	 * @param args
	 *          The program expects only one argument - the action to execute.
	 *          Available actions are:
	 *          <dl>
	 *          <dt>run</dt>
	 *          <dd>To launch the GUI.</dd>
	 *          <dt>import</dt>
	 *          <dd>To parse data set into the SQL database.</dd>
	 *          <dt>cache</dt>
	 *          <dd>To pre-generate all the schedule images.
	 *          <dd>
	 *          </dl>
	 */
	public static void main(final String[] args) {
		if (args.length != 1) {
			Main.printUsageAndExit();
		}
		if ("run".equals(args[0]) || "cache".equals(args[0])) {
			File dbFile = Configuration.getDatabaseFile();
			if (dbFile.exists()) {
				Database.use();
			} else {
				System.out.print("Database file " + dbFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			if ("run".equals(args[0])) {
				Main.main.gui();
			} else {
				Main.main.cache();
			}
		} else {
			File machinesFile = Configuration.getMachinesFile();
			if (!machinesFile.exists()) {
				System.out.print("Machines file " + machinesFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			File dataFile = Configuration.getEventsFile();
			if (!dataFile.exists()) {
				System.out.print("Machines file " + dataFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			Database.use();
			Main.main.importData(new Importer(machinesFile, dataFile));
		}
	}

	/**
	 * Prints insctructions on how to use the program and exits.
	 */
	public static void printUsageAndExit() {
		System.out.println("Please choose one of the operations available: ");
		System.out.println(" ant import");
		System.out.println(" ant cache");
		System.out.println(" ant run");
		System.exit(1);
	}

	/**
	 * Launches a process of pre-generating schedule images so that they are
	 * available later for the GUI.
	 * 
	 * A rendering pipeline is opened and filled, at which point the thread waits
	 * for the renderer to wake it when the pipeline is getting empty. When woken
	 * up, the thread adds more tasks to the pipeline, waits and so on until all
	 * the schedules are rendered.
	 * 
	 * The renderer wakes the thread through a listener that this class set on
	 * renderer's progress property.
	 */
	private synchronized void cache() {
		System.out.println("Gathering data for rendering...");
		Set<Machine> machines = Machine.getAllGroupless();
		Set<Integer> ticks = Event.getAllTicks();
		Main.totalRenderers = ticks.size() * machines.size();
		Integer initialRenderers = 0;
		System.out.println("Submitting schedules for rendering...");
		ExecutorService e = Executors.newFixedThreadPool(Main.MAX_RENDERER_THREADS);
		Main.startProcessingTime = Double.valueOf(System.nanoTime());
		Main.lastReportTime = Main.startProcessingTime.longValue();
		for (Integer clock : ticks) {
			for (Machine m : machines) {
				ScheduleRenderer mr = new ScheduleRenderer(m, clock, e, true, Main.main);
				e.submit(mr);
				Main.queuedRenderers.add(mr.hashCode());
				if (Main.queuedRenderers.size() > Main.MAX_QUEUED_RENDERERS) {
					try {
						Main.logger.debug("Enqueued "
						    + (Main.queuedRenderers.size() - initialRenderers)
						    + " more renderers.");
						Main.main.wait();
						initialRenderers = Main.queuedRenderers.size();
					} catch (InterruptedException ex) {
						// do nothing
					}
				}
			}
		}

		System.out
		    .println("Please wait while the rest of the schedules are being rendered...");
		System.out.println("");
		e.shutdown();
		while (Main.queuedRenderers.size() > 0) {
			try {
				Main.main.wait();
			} catch (InterruptedException ex) {
				// do nothing
			}
		}

		Double time = (System.nanoTime() - Main.startProcessingTime) / 1000 / 1000 / 1000;
		System.out.println("Rendering successfully finished.");
		System.out.println("Took " + new PrintfFormat("%.2f").sprintf(time)
		    + " seconds.");

		System.exit(0);
	}

	/**
	 * MainFrame method for the whole project.
	 * 
	 * @param args
	 */
	private void gui() {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main.frame = new MainFrame();
				Main.frame.setVisible(true);
			}
		});
	}

	/**
	 * Launches the process of importing data from the data set to the SQL
	 * database.
	 * 
	 * @param i
	 *          The importer that handles the actual work.
	 */
	private void importData(final Importer i) {
		System.out.println("Importing specified data.");
		System.out.println("");
		Executors.newCachedThreadPool().submit(i);
		System.out.println("Processing...");
		while (!i.isDone()) {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				// do nothing
			}
			System.out.println(" " + i.getProgress() + " % completed...");
		}
		System.out.println("");
		if (i.isSuccess()) {
			System.out.println("Import finished successfully!");
			System.exit(0);
		} else {
			System.out.println("Import failed!");
			System.exit(1);
		}
	}

	/**
	 * A method to handle changes in the progress of caching schedules. Its main
	 * task is to wake the main thread when the rendering queue is running empty
	 * and to report the progress.
	 */
	@Override
	public synchronized void propertyChange(final PropertyChangeEvent evt) {
		Double leastPossibleQueueLength = (Main.MAX_QUEUED_RENDERERS - Main.MAX_RENDERER_THREADS) * 0.5;
		if (Main.queuedRenderers.size() < leastPossibleQueueLength) {
			// wake up the main thread to add some more renderers to the queue
			this.notifyAll();
			if (Main.queuedRenderers.size() == 0) {
				// no need to run through this whole method when there are no renderers
				return;
			}
		}
		Double criticallyLowQueueLength = leastPossibleQueueLength / 5;
		if (Main.queuedRenderers.size() < criticallyLowQueueLength) {
			// should the queue not be long enough, work harder to enlarge it
			if ((System.nanoTime() - Main.lastDoubledQueueLength) > 2000000000) {
				Main.logger.info("Doubling renderer queue length.");
				Main.MAX_QUEUED_RENDERERS = Main.MAX_QUEUED_RENDERERS * 2;
				// but only once every two seconds.
				Main.lastDoubledQueueLength = System.nanoTime();
			}
		}
		ScheduleRenderer m = (ScheduleRenderer) evt.getSource();
		if (m.isDone() && !m.isCancelled()) {
			if (!Main.queuedRenderers.remove(m.hashCode())) {
				// if the renderer is already removed, don't show progress information
				// for it
				return;
			}
			Main.doneRenderers++;
			Integer perMille = Main.totalRenderers / 1000;
			if (Main.doneRenderers % perMille == 0) {
				Long timeItTook = (System.nanoTime() - Main.lastReportTime);
				Main.lastReportTime = System.nanoTime();
				// show some progress
				Double percentage = (Main.doneRenderers / (double) Main.totalRenderers);
				Double timeLeft = Main.countProgress(Double.valueOf(timeItTook),
				    percentage, perMille, 25);
				System.out.println(new PrintfFormat("%.2f").sprintf(percentage * 100)
				    + " % ("
				    + (Main.doneRenderers)
				    + "/"
				    + Main.totalRenderers
				    + ") done in "
				    + new PrintfFormat("%.1f")
				        .sprintf(timeItTook / 1000.0 / 1000.0 / 1000.0)
				    + "s, need "
				    + new PrintfFormat("%.0f")
				        .sprintf(timeLeft / 1000.0 / 1000.0 / 1000.0) + "s more.");
			}
		}
	}
}
