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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
public final class Main {

    private static MainFrame frame;

    private static void cache() {
	ExecutorService e = Executors.newFixedThreadPool(20);

	System.out.println("Gathering data for rendering...");
	Set<Machine> machines = new HashSet<Machine>(Machine.getAllGroupless());
	// get all ticks
	Set<Integer> ticks = new HashSet<Integer>();
	Integer currentClock = 0;
	Event currentEvent = null;
	while ((currentEvent = Event.getNext(currentClock)) != null) {
	    currentClock = currentEvent.getClock();
	    ticks.add(currentClock);
	}

	System.out.println("Submitting schedules for rendering...");
	Integer processedTicks = 0;
	Set<MachineRenderer> rs = Collections.synchronizedSet(new HashSet<MachineRenderer>());
	Double time = new Double(System.nanoTime());
	for (Integer clock : ticks) {
	    for (Machine m : machines) {
		MachineRenderer r = new MachineRenderer(m, clock);
		e.submit(r);
		rs.add(r);
	    }
	    processedTicks++;
	}

	e.shutdown();
	Integer totalSchedules = machines.size() * ticks.size();
	Long startProcessingTime = System.nanoTime();
	while (!e.isTerminated()) {
	    try {
		Thread.sleep(5000);
	    } catch (InterruptedException ex) {
		// do nothing
	    }
	    Set<MachineRenderer> rs2 = new HashSet<MachineRenderer>(rs);
	    for (MachineRenderer r : rs) {
		if (r.isDone()) {
		    rs2.remove(r);
		}
	    }
	    rs = rs2;
	    Double percentage = (new Double(rs.size()) / new Double(
		    totalSchedules)) * 100;
	    Long processingTime = System.nanoTime() - startProcessingTime;
	    Double timeLeft = (processingTime * (100 / (100 - percentage))) / 1000 / 1000 / 1000;
	    System.out.println(percentage + " % (" + rs.size() + " out of "
		    + totalSchedules + " schedules) left to render. Estimated "
		    + timeLeft + " more second left.");
	}

	time = (System.nanoTime() - time) / 1000 / 1000 / 1000;
	System.out.println("Rendering successfully finished.");
	System.out.println("Took " + time + " seconds.");

	System.exit(0);
    }

    public static MainFrame getFrame() {
	return Main.frame;
    }

    /**
     * MainFrame method for the whole project.
     * 
     * @param args
     */
    private static void gui() {
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

    private static void importData(final Importer i) {
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
		Main.gui();
	    } else {
		Main.cache();
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
	    if (!dataFile.exists()) {
		System.out.print("Dataset file " + dataFile.getAbsolutePath()
			+ " cannot be found! ");
		Main.printUsageAndExit();
	    }
	    Main.importData(new Importer(machinesFile, dataFile, args[3]));
	}
    }
    public static void printUsageAndExit() {
	System.out
	.println("Please choose one of the operations available: ");
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

}
