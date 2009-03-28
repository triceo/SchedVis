/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

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
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.Database;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.rendering.MachineRenderer;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main implements PropertyChangeListener {

    private static final Main main = new Main();

    private static MainFrame frame;

    private static Integer doneRenderers = 0;
    private static Integer totalRenderers = 0;
    private static Integer queuedRenderers = 0;

    private static final Integer MAX_RENDERER_THREADS = 32;
    private static final Integer MAX_QUEUED_RENDERERS = Main.MAX_RENDERER_THREADS * 4;

    private static Long lastReportTime;

    private static Logger logger = Logger.getLogger(Main.class);

    private static Double startProcessingTime;

    private static Queue<Double> lastProcessingTimes = new LinkedList<Double>();

    /**
     * Estimate a remaining time that a job will take.
     * 
     * @param lastUnitTook
     *            How long did last unit take. In nanoseconds.
     * @param alreadyDonePct
     *            How many units are already done. In percent.
     * @param unitSize
     *            How many renderable items does a unit contain.
     * @param historySize
     *            How many recent unit execution times should be taken into
     *            account.
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
	Double averageTime = totalTime / (double) historySize;
	return ((1 - alreadyDonePct) * (Main.totalRenderers / unitSize))
	* averageTime;
    }

    public static MainFrame getFrame() {
	return Main.frame;
    }

    public static void main(final String[] args) {
	if (args.length < 1) {
	    Main.printUsageAndExit();
	}
	if ("run".equals(args[0]) || "cache".equals(args[0])) {
	    if (args.length != 2) {
		Main.printUsageAndExit();
	    }
	    File dbFile = new File(args[1]);
	    if (dbFile.exists()) {
		Database.use(dbFile.getAbsolutePath());
	    } else {
		System.out.print("Database file " + dbFile.getAbsolutePath()
			+ "cannot be found! ");
		Main.printUsageAndExit();
	    }
	    if ("run".equals(args[0])) {
		Main.main.gui();
	    } else {
		Main.main.cache();
	    }
	} else {
	    if (args.length != 4) {
		Main.printUsageAndExit();
	    }
	    File machinesFile = new File(args[1]);
	    if (!machinesFile.exists()) {
		System.out
		.print("Machines file "
			+ machinesFile.getAbsolutePath()
			+ " cannot be found! ");
		Main.printUsageAndExit();
	    }
	    File dataFile = new File(args[2]);
	    File dbFile = new File(args[3]);
	    Database.use(dbFile.getAbsolutePath());
	    Main.main.importData(new Importer(machinesFile, dataFile));
	}
    }

    public static void printUsageAndExit() {
	System.out.println("Please choose one of the operations available: ");
	System.out
	.println(" ant import -Dmachines=<machineFileName> -Devents=<datasetFileName> -Ddatabase=<databaseName>");
	System.out.println(" ant cache -Ddatabase=<databaseFileName>");
	System.out.println(" ant run -Ddatabase=<databaseFileName>");
	System.exit(1);
    }

    public static void update() {
	if (Main.frame != null) {
	    Main.frame.update();
	}
    }

    private synchronized void cache() {
	ExecutorService e = Executors
	.newFixedThreadPool(Main.MAX_RENDERER_THREADS);

	System.out.println("Gathering data for rendering...");
	Set<Machine> machines = new HashSet<Machine>(Machine.getAllGroupless());

	System.out.println("Submitting schedules for rendering...");
	Main.startProcessingTime = Double.valueOf(System.nanoTime());
	Main.lastReportTime = Main.startProcessingTime.longValue();
	List<Integer> ticks = Event.getAllTicks();
	Main.totalRenderers = ticks.size() * machines.size();
	Integer initialRenderers = 0;
	for (Integer clock : ticks) {
	    for (Machine m : machines) {
		e.submit(new MachineRenderer(m, clock, true, Main.main));
		Main.queuedRenderers++;
		if (Main.queuedRenderers > Main.MAX_QUEUED_RENDERERS) {
		    try {
			Main.logger.debug("Enqueued "
				+ (Main.queuedRenderers - initialRenderers)
				+ " more renderers.");
			Main.main.wait();
			initialRenderers = Main.queuedRenderers;
		    } catch (InterruptedException ex) {
			// do nothing
		    }
		}
	    }
	}

	System.out
	.println("Please wait while the rest of the schedules are being rendered...");
	e.shutdown();
	while (!e.isTerminated()) {
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
	try {
	    // Set System L&F
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (final UnsupportedLookAndFeelException e) {
	    // handle exception
	} catch (final ClassNotFoundException e) {
	    // handle exception
	} catch (final InstantiationException e) {
	    // handle exception
	} catch (final IllegalAccessException e) {
	    // handle exception
	}
	// Schedule a job for the event-dispatching thread:
	// creating and showing this application's GUI.
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		Main.frame = new MainFrame();
		Main.frame.setVisible(true);
	    }
	});
    }

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

    @Override
    public synchronized void propertyChange(final PropertyChangeEvent evt) {
	Double leastPossibleQueueLength = (Main.MAX_QUEUED_RENDERERS - Main.MAX_RENDERER_THREADS) * 0.5;
	if (Main.queuedRenderers < leastPossibleQueueLength) {
	    // wake up the main thread to add some more renderers to the queue
	    this.notifyAll();
	}
	if (((MachineRenderer) evt.getSource()).isDone()) {
	    Main.doneRenderers++;
	    Main.queuedRenderers--;
	    Integer perMille = Main.totalRenderers / 1000;
	    if (Main.doneRenderers % perMille == 0) {
		Long timeItTook = (System.nanoTime() - Main.lastReportTime);
		Main.lastReportTime = System.nanoTime();
		// show some progress
		Double percentage = (Main.doneRenderers / (double) Main.totalRenderers);
		Double timeLeft = Main.countProgress(
			Double.valueOf(timeItTook), percentage, perMille, 10);
		System.out.println(new PrintfFormat("%.2f")
		.sprintf(percentage * 100)
		+ " % ("
		+ (Main.doneRenderers)
		+ "/"
		+ Main.totalRenderers
		+ ") done in "
		+ new PrintfFormat("%.1f")
		.sprintf(timeItTook / 1000.0 / 1000.0 / 1000.0)
		+ "s, need "
		+ new PrintfFormat("%.0f")
		.sprintf(timeLeft / 1000.0 / 1000.0 / 1000.0)
		+ "s more.");
	    }
	}
	if (Main.queuedRenderers == 0) {
	    // notify the main thread that there are no renderers left
	    Main.logger.debug("Waking main thread with no more renderers.");
	    this.notifyAll();
	}
    }

}
