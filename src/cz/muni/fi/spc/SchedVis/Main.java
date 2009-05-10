/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.background.Importer;
import cz.muni.fi.spc.SchedVis.background.Player;
import cz.muni.fi.spc.SchedVis.background.ScheduleRenderer;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.Database;
import cz.muni.fi.spc.SchedVis.util.ScheduleRenderingController;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main {

	/**
	 * The actual class creating the GUI.
	 * 
	 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
	 * 
	 */
	private static final class GUI implements Runnable {
		public void run() {
			Main.frame = new MainFrame();
			Main.frame.setVisible(true);
		}
	}

	private static final Main main = new Main();

	private static MainFrame frame;

	/**
	 * Runs some basic benchmarks. Basically renders some random schedules many,
	 * many, many times and outputs the resulting time.
	 */
	public static void benchmark() {
		final Set<Machine> machines = Machine.getAllGroupless();
		new ScheduleRenderer(machines.toArray(new Machine[] {})[0], 1);
		Double totalTime = 0.0;
		final ExecutorService e = Executors.newFixedThreadPool(1);
		final int clockCount = Event.getAllTicks().size() / 500;
		final Integer tickSpace = Math.max(1, Event.getAllTicks().size()
		    / clockCount);
		final int[] clocks = new int[clockCount];
		clocks[0] = 1;
		for (int i = 1; i < clockCount; i++) {
			clocks[i] = tickSpace * i;
		}
		for (final Integer clock : clocks) {
			final Long now = System.nanoTime();
			for (final Machine m : machines) {
				final ScheduleRenderer mr = new ScheduleRenderer(m, clock);
				e.submit(mr);
				try {
					mr.get();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
			final Double time = (System.nanoTime() - (double) now) / 1000 / 1000 / 1000;
			totalTime += time;
			System.out.println("Clock #" + clock + ": " + time);
		}
		System.out.println("");
		System.out.println("Per clock: " + (totalTime / clockCount));
		System.out.println("Per machine: "
		    + (totalTime / clockCount / machines.size()));
		return;
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
	 * Main method for the application. Checks input parameters, passes control to
	 * other parts of this class.
	 * 
	 * @param args
	 *          The program expects only one argument - the action to execute.
	 *          Available actions are:
	 *          <dl>
	 *          <dt>run</dt>
	 *          <dd>To launch the GUI.</dd>
	 *          <dt>import</dt>
	 *          <dd>To parse data set into the SQL database.</dd>
	 *          <dt>benchmark</dt>
	 *          <dd>To launch the benchmarking tool.</dd>
	 *          <dd>
	 *          </dl>
	 */
	public static void main(final String[] args) {
		if (args.length != 1) {
			Main.printUsageAndExit();
		}
		if ("benchmark".equals(args[0])) {
			Database.use();
			Main.benchmark();
			System.exit(0);
			return;
		} else if ("run".equals(args[0])) {
			final File dbFile = Configuration.getDatabaseFile();
			if (dbFile.exists()) {
				Database.use();
			} else {
				System.out.print("Database file " + dbFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			Main.main.gui();
		} else {
			final File machinesFile = Configuration.getMachinesFile();
			if (!machinesFile.exists()) {
				System.out.print("Machines file " + machinesFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			final File dataFile = Configuration.getEventsFile();
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
		System.out.println(" ant run");
		System.exit(1);
	}

	/**
	 * MainFrame method for the whole project.
	 * 
	 * @param args
	 */
	private void gui() {
		/*
		 * Run a first batch of rendering tasks so that the caches fill themselves
		 * up.
		 */
		System.out.println("Please wait while some data are being cached...");
		final Event evt = Event.getFirst();
		for (final Machine m : Machine.getAllGroupless()) {
			ScheduleRenderingController.render(m, evt.getVirtualClock());
		}
		System.out.println("Done...");
		Executors.newFixedThreadPool(1).submit(Player.getInstance());
		/*
		 * Schedule a job for the event-dispatching thread creating and showing this
		 * application's GUI.
		 */
		javax.swing.SwingUtilities.invokeLater(new GUI());
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
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
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
			Configuration.getDatabaseFile().deleteOnExit();
			System.exit(1);
		}
	}

}
