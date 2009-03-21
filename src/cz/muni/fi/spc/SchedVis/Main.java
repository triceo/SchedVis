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
import java.util.List;
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
public final class Main {

    private static MainFrame frame;

    /**
     * Not functional for now.
     */
    private static void cache() {
	ExecutorService e = Executors.newCachedThreadPool();

	System.out.println("Caching schedule images...");
	List<Machine> machines = Machine.getAllGroupless();
	Integer totalEvents = Event.getLast().getId();
	for (int i = 1; i <= totalEvents; i++) {
	    for (Machine m : machines) {
		MachineRenderer r = new MachineRenderer(m, i);
		Logger.getLogger(Main.class).info(
			"Machine " + m.getName() + " at time " + i
			+ " passed to rendering.");
		e.submit(r);
	    }
	}
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
	    System.out.println("A");
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
