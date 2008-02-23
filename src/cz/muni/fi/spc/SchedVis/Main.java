/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import javax.swing.JFrame;

import cz.muni.fi.spc.SchedVis.ui.MainWindow;

/**
 * The main class for the SchedVis project.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class Main {

	/**
	 * MainWindow method for the whole project.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		MainWindow.main(null);
	}

}
