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

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cz.muni.fi.spc.SchedVis.ui.MainFrame;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main {

    private static MainFrame frame;

    public static MainFrame getFrame() {
	return Main.frame;
    }

    /**
     * MainFrame method for the whole project.
     * 
     * @param args
     */
    public static void main(final String[] args) {
	/*
	 * try { System.setOut(new PrintStream(new BufferedOutputStream( new
	 * FileOutputStream("schedvis.out")), true)); } catch (final Exception
	 * e) { Logger.getLogger(Main.class).error(
	 * "Cannot redirect standard output to a file!"); } try {
	 * System.setErr(new PrintStream(new BufferedOutputStream( new
	 * FileOutputStream("schedvis.err")), true)); } catch (final Exception
	 * e) { Logger.getLogger(Main.class).error(
	 * "Cannot redirect error output to a file!"); }
	 */
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

    public static void update() {
	if (Main.frame != null) {
	    Main.frame.update();
	}
    }

}
