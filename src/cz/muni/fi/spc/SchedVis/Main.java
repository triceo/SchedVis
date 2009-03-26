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
public final class Main implements PropertyChangeListener {

    private static final Main main = new Main();

    private static MainFrame frame;

    private static Integer activeRenderers = 0;
    private static Integer totalRenderers = 0;

    private static void cache() {
	ExecutorService e = Executors.newFixedThreadPool(32);

	System.out.println("Gathering data for rendering...");
	Set<Machine> machines = new HashSet<Machine>(Machine.getAllGroupless());

	System.out.println("Submitting schedules for rendering...");
	Double startProcessingTime = Double.valueOf(System.nanoTime());
	for (Integer clock : Event.getAllTicks()) {
	    for (Machine m : machines) {
		e.submit(new MachineRenderer(m, clock, true, Main.main));
		Main.activeRenderers++;
		Main.totalRenderers++;
	    }
	}

	e.shutdown();
	System.out.println("Rendering in progress...");
	while (!e.isTerminated()) {
	    try {
		Thread.sleep(5000);
	    } catch (InterruptedException ex) {
		// do nothing
	    }
	    // show some progress
	    Double percentage = (Main.activeRenderers / (double) Main.totalRenderers) * 100;
	    System.out.println(percentage + " % schedules ("
		    + Main.activeRenderers + "/" + Main.totalRenderers
		    + ") left.");
	}

	Double time = (System.nanoTime() - startProcessingTime) / 1000 / 1000 / 1000;
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
	    File dbFile = new File(args[3]);
	    if (dbFile.exists()) {
		Database.use(dbFile.getAbsolutePath());
	    } else {
		System.out.print("Database file " + dbFile.getAbsolutePath()
			+ "cannot be found! ");
		Main.printUsageAndExit();
	    }
	    Main.importData(new Importer(machinesFile, dataFile));
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

    @Override
    public synchronized void propertyChange(final PropertyChangeEvent evt) {
	if (((MachineRenderer) evt.getSource()).isDone()) {
	    Main.activeRenderers--;
	}
    }

}
