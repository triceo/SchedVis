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

	/**
	 * MainFrame method for the whole project.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
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
