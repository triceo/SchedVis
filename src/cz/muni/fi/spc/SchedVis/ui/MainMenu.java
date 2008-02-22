/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cz.muni.fi.spc.SchedVis.model.SQL;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MainMenu implements UIElement, ActionListener, ItemListener {

	private static String ACTION_QUIT = "quit";
	private static String ACTION_NEW = "new";
	private final JMenuBar menuBar;

	/**
	 * Class constructor. Creates the whole menu thing.
	 */
	public MainMenu() {
		// Create the menu bar.
		this.menuBar = new JMenuBar();
		JMenu menu = null;
		JMenuItem menuItem = null;

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical file menu as seen in other apps.");
		this.menuBar.add(menu);

		// a group of JMenuItems
		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Process new batch of data.");
		menuItem.setActionCommand(MainMenu.ACTION_NEW);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Open...", KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Open an already processed batch of data.");
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save the current configuration.");
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Terminates the simulation and exits the application.");
		menuItem.setActionCommand(MainMenu.ACTION_QUIT);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Build the first menu.
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical edit options.");
		this.menuBar.add(menu);

		// Build the first menu.
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription(
				"The classical support options.");
		this.menuBar.add(menu);

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
		if (command.equals(MainMenu.ACTION_QUIT)) {
			this.quit();
		} else if (command.equals(MainMenu.ACTION_NEW)) {
			try {
				SQL.getInstance("testing");
			} catch (final Exception e) {
				System.err.println("Cannot instantiate the model, exiting.");
				e.printStackTrace();
				this.quit();
			}
		}
	}

	@Override
	public JMenuBar get() {
		return this.menuBar;
	}

	public void itemStateChanged(final ItemEvent e) {
	}

	protected void quit() {
		System.exit(0);
	}

}
