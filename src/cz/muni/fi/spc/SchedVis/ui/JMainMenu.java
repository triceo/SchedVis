/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JMainMenu extends JMenuBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -301105021169477153L;
	
	private static String ACTION_QUIT = "quit";
	private static String ACTION_NEW = "new";
	private static String ACTION_MANAGE_GROUPS = "manage_groups";

	private final JFrame frame;

	/**
	 * Class constructor. Creates the whole menu thing.
	 */
	public JMainMenu(final JFrame frame) {
		// Create the menu bar.
		this.frame = frame;
		JMenu menu = null;
		JMenuItem menuItem = null;

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical file menu as seen in other apps.");
		this.add(menu);

		// a group of JMenuItems
		menuItem = new JMenuItem("New data source", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Process new batch of data.");
		menuItem.setActionCommand(JMainMenu.ACTION_NEW);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Terminates the simulation and exits the application.");
		menuItem.setActionCommand(JMainMenu.ACTION_QUIT);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Build the first menu.
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical edit options.");
		this.add(menu);

		menuItem = new JMenuItem("Manage groups...", KeyEvent.VK_G);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Opens a dialog used to manage machine groups.");
		menuItem.setActionCommand(JMainMenu.ACTION_MANAGE_GROUPS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Build the first menu.
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical support options.");
		this.add(menu);

		menuItem = new JMenuItem("Help contents...", KeyEvent.VK_H);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Shows a help dialog.");
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("About SchedViz...", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Shows an about screen.");
		menu.add(menuItem);
	}

	public void actionPerformed(final ActionEvent event) {
		final String command = event.getActionCommand();
		if (command.equals(JMainMenu.ACTION_QUIT)) {
			this.quit();
		} else if (command.equals(JMainMenu.ACTION_NEW)) {
			final ImportDialog dialog = new ImportDialog(this.frame, true);
			dialog.setVisible(true);
		} else if (command.equals(JMainMenu.ACTION_MANAGE_GROUPS)) {
			final GroupsDialog dialog = new GroupsDialog(this.frame, true);
			dialog.setVisible(true);
		}
	}

	protected void quit() {
		System.exit(0);
	}

}
