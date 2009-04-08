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
public class MainMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -301105021169477153L;

	private static String ACTION_QUIT = "quit";
	private static String ACTION_MANAGE_GROUPS = "manage_groups";

	private final JFrame frame;

	/**
	 * Class constructor. Creates the whole menu thing.
	 */
	public MainMenu(final JFrame frame) {
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
		this.add(menu);

		menuItem = new JMenuItem("Manage groups...", KeyEvent.VK_G);
		menuItem.getAccessibleContext().setAccessibleDescription(
		    "Opens a dialog used to manage machine groups.");
		menuItem.setActionCommand(MainMenu.ACTION_MANAGE_GROUPS);
		menuItem.addActionListener(this);
		menu.add(menuItem);
	}

	/**
	 * Handler for the clicks on menu items.
	 */
	public void actionPerformed(final ActionEvent event) {
		final String command = event.getActionCommand();
		if (command.equals(MainMenu.ACTION_QUIT)) {
			this.quit();
		} else if (command.equals(MainMenu.ACTION_MANAGE_GROUPS)) {
			final GroupsDialog dialog = new GroupsDialog(this.frame, true);
			dialog.setVisible(true);
		}
	}

	/**
	 * Terminate the application.
	 */
	protected void quit() {
		System.exit(0);
	}

}
