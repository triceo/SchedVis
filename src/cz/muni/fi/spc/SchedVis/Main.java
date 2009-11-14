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
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.background.Importer;
import cz.muni.fi.spc.SchedVis.background.Player;
import cz.muni.fi.spc.SchedVis.background.ScheduleRenderer;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.ui.MainFrame;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.Database;
import cz.muni.fi.spc.SchedVis.util.Messages;
import cz.muni.fi.spc.SchedVis.util.PrintfFormat;
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
		Double totalTime = 0.0;
		final int tickCount = Event.getAllTicks().size() / 500;
		final Integer tickSpace = Math.max(1, Event.getAllTicks().size()
		    / tickCount);
		final int[] ticks = new int[tickCount];
		ticks[0] = 1;
		for (int i = 1; i < tickCount; i++) {
			ticks[i] = tickSpace * i;
		}
		// warmup period
		Main.warmup();
		System.out.println(Messages.getString("Main.0")); //$NON-NLS-1$
		System.out.println();
		// run!
		Integer i = 0;
		for (final Integer tick : ticks) {
			i++;
			final Long now = System.nanoTime();
			for (final Machine m : machines) {
				ScheduleRenderingController.render(m, Event.getWithId(tick));
			}
			final Double time = (System.nanoTime() - (double) now) / 1000 / 1000 / 1000;
			totalTime += time;
			System.out.println(new PrintfFormat(Messages.getString("Main.1")) //$NON-NLS-1$
			    .sprintf(new Integer[] { i, ticks.length, tick }));
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
				// do nothing
			}
		}
		System.out.println();
		System.out.println(new PrintfFormat(Messages.getString("Main.2")) //$NON-NLS-1$
		    .sprintf(totalTime / tickCount));
		System.out.println(new PrintfFormat(Messages.getString("Main.3")) //$NON-NLS-1$
		    .sprintf(totalTime / tickCount / machines.size()));
		System.out.println();
		System.out.println(Messages.getString("Main.4")); //$NON-NLS-1$
		ScheduleRenderer.reportLogResults();
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
		if ("benchmark".equals(args[0])) { //$NON-NLS-1$
			Database.use();
			Main.benchmark();
			System.exit(0);
			return;
		} else if ("run".equals(args[0])) { //$NON-NLS-1$
			final File dbFile = Configuration.getDatabaseFile();
			if (dbFile.exists()) {
				Database.use();
			} else {
				System.out.print(new PrintfFormat(Messages.getString("Main.7")) //$NON-NLS-1$
				    .sprintf(dbFile.getAbsolutePath()));
				Main.printUsageAndExit();
			}
			Main.main.gui();
		} else {
			final File machinesFile = Configuration.getMachinesFile();
			if (!machinesFile.exists()) {
				System.out.print(new PrintfFormat(Messages.getString("Main.8")) //$NON-NLS-1$
				    .sprintf(machinesFile.getAbsolutePath()));
				Main.printUsageAndExit();
			}
			final File dataFile = Configuration.getEventsFile();
			if (!dataFile.exists()) {
				System.out.print(new PrintfFormat(Messages.getString("Main.9")) //$NON-NLS-1$
				    .sprintf(dataFile.getAbsolutePath()));
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
		System.out.println(Messages.getString("Main.10")); //$NON-NLS-1$
		System.out.println(" ant import"); //$NON-NLS-1$
		System.out.println(" ant run"); //$NON-NLS-1$
		System.exit(1);
	}

	private static void warmup() {
		System.out.println(Messages.getString("Main.13")); //$NON-NLS-1$
		final Event evt = Event.getFirst();
		for (final Machine m : Machine.getAllGroupless()) {
			ScheduleRenderingController.getRendered(m, evt);
		}
		ScheduleRenderer.clearLogResults();
	}

	/**
	 * MainFrame method for the whole project.
	 * 
	 * @param args
	 */
	private void gui() {
		Main.warmup();
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
		System.out.println(Messages.getString("Main.14")); //$NON-NLS-1$
		System.out.println();
		Executors.newCachedThreadPool().submit(i);
		System.out.println(Messages.getString("Main.15")); //$NON-NLS-1$
		while (!i.isDone()) {
			try {
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
				// do nothing
			}
			System.out
			    .println(new PrintfFormat(Messages.getString("Main.16")).sprintf(i //$NON-NLS-1$
			        .getProgress()));
		}
		System.out.println();
		if (i.isSuccess()) {
			System.out.println(Messages.getString("Main.17")); //$NON-NLS-1$
			System.exit(0);
		} else {
			System.out.println(Messages.getString("Main.18")); //$NON-NLS-1$
			Configuration.getDatabaseFile().deleteOnExit();
			System.exit(1);
		}
	}
}
