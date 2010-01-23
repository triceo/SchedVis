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
import java.util.Formatter;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;
import cz.muni.fi.spc.SchedVis.util.Benchmark;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.Database;
import cz.muni.fi.spc.SchedVis.util.Importer;
import cz.muni.fi.spc.SchedVis.util.Player;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

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
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			Main.printUsageAndExit();
		}
		if ("benchmark".equals(args[0])) {
			Database.use();
			Main.warmup();
			Benchmark.run();
			System.exit(0);
			return;
		} else if ("run".equals(args[0])) {
			final File dbFile = Configuration.getDatabaseFile();
			if (dbFile.exists()) {
				Database.use();
			} else {
				System.out.print(new Formatter().format(Messages.getString("Main.7"),
				    dbFile.getAbsolutePath()));
				Main.printUsageAndExit();
			}
			Main.warmup();
			Main.main.gui();
		} else {
			final File machinesFile = Configuration.getMachinesFile();
			if (!machinesFile.exists()) {
				System.out.print(new Formatter().format(Messages.getString("Main.8"),
				    machinesFile.getAbsolutePath()));
				Main.printUsageAndExit();
			}
			final File dataFile = Configuration.getEventsFile();
			if (!dataFile.exists()) {
				System.out.print(new Formatter().format(Messages.getString("Main.9"),
				    dataFile.getAbsolutePath()));
				Main.printUsageAndExit();
			}
			Database.use();
			Main.main.importData(new Importer(machinesFile, dataFile));
		}
	}

	/**
	 * Prints instructions on how to use the program and exits.
	 */
	public static void printUsageAndExit() {
		System.out.println(Messages.getString("Main.10"));
		System.out.println(" ant import");
		System.out.println(" ant run");
		System.exit(1);
	}

	private static void warmup() throws Exception {
		System.out.println(Messages.getString("Main.13"));
		final Event evt = Event.getFirst();
		for (final Machine m : Machine.getAllGroupless()) {
			Benchmark.runSingleSchedule(evt, m);
		}
		Benchmark.clearLogResults();
	}

	/**
	 * MainFrame method for the whole project.
	 * 
	 * @param args
	 */
	private void gui() {
		Executors.newFixedThreadPool(1).submit(Player.getInstance());
		/*
		 * Schedule a job for the event-dispatching thread creating and showing this
		 * application's GUI.
		 */
		SwingUtilities.invokeLater(new GUI());
	}

	/**
	 * Launches the process of importing data from the data set to the SQL
	 * database.
	 * 
	 * @param i
	 *          The importer that handles the actual work.
	 */
	private void importData(final Importer i) {
		System.out.println(Messages.getString("Main.14"));
		System.out.println();
		Executors.newCachedThreadPool().submit(i);
		System.out.println(Messages.getString("Main.15"));
		while (!i.isDone()) {
			try {
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
				// do nothing
			}
			System.out.println(new Formatter().format(Messages.getString("Main.16"),
			    i.getProgress()));
		}
		System.out.println();
		if (i.isSuccess()) {
			System.out.println(Messages.getString("Main.17"));
			System.exit(0);
		} else {
			System.out.println(Messages.getString("Main.18"));
			Configuration.getDatabaseFile().deleteOnExit();
			System.exit(1);
		}
	}
}
