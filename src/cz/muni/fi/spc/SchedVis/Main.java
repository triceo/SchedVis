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

import java.io.File;
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.model.Database;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main {

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
	 *          <dd>
	 *          </dl>
	 */
	public static void main(final String[] args) {
		if (args.length != 1) {
			Main.printUsageAndExit();
		}
		if ("benchmark".equals(args[0])) {
			Database.use();
			Benchmark.run();
			System.exit(0);
			return;
		} else if ("run".equals(args[0])) {
			File dbFile = Configuration.getDatabaseFile();
			if (dbFile.exists()) {
				Database.use();
			} else {
				System.out.print("Database file " + dbFile.getAbsolutePath()
				    + " cannot be found! ");
				Main.printUsageAndExit();
			}
			Main.main.gui();
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
		System.out.println(" ant run");
		System.exit(1);
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
				Thread.sleep(5000);
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

}
