/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.ui.MainFrame;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main {

    private static MainFrame frame;

    /**
     * MainFrame method for the whole project.
     * 
     * @param args
     */
    public static void main(final String[] args) {
	try {
	    System.setOut(new PrintStream(new BufferedOutputStream(
		    new FileOutputStream("schedvis.out"))));
	} catch (final Exception e) {
	    Logger.getLogger(Main.class).warn(
		    "Cannot redirect standard output. Messages may get lost.");
	}
	try {
	    System.setErr(new PrintStream(new BufferedOutputStream(
		    new FileOutputStream("schedvis.err"))));
	} catch (final Exception e) {
	    Logger.getLogger(Main.class).warn(
		    "Cannot redirect error output. Messages may get lost.");
	}
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
