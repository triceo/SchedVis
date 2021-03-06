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

import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MainMenu extends JMenuBar implements ActionListener {

	private static enum Actions {
		QUIT, MANAGE_GROUPS;
	}

	private static final long serialVersionUID = -301105021169477153L;

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
		menu = new JMenu(Messages.getString("MainMenu.2"));
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
		    Messages.getString("MainMenu.3"));
		this.add(menu);

		menuItem = new JMenuItem(Messages.getString("MainMenu.4"), KeyEvent.VK_Q);
		menuItem.getAccessibleContext().setAccessibleDescription(
		    Messages.getString("MainMenu.5"));
		menuItem.setActionCommand(Actions.QUIT.toString());
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Build the first menu.
		menu = new JMenu(Messages.getString("MainMenu.6"));
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription(
		    Messages.getString("MainMenu.7"));
		this.add(menu);

		menuItem = new JMenuItem(Messages.getString("MainMenu.8"), KeyEvent.VK_G);
		menuItem.getAccessibleContext().setAccessibleDescription(
		    Messages.getString("MainMenu.9"));
		menuItem.setActionCommand(Actions.MANAGE_GROUPS.toString());
		menuItem.addActionListener(this);
		menu.add(menuItem);
	}

	/**
	 * Handler for the clicks on menu items.
	 */
	public void actionPerformed(final ActionEvent event) {
		final String command = event.getActionCommand();
		if (command.equals(Actions.QUIT.toString())) {
			this.quit();
		} else if (command.equals(Actions.MANAGE_GROUPS.toString())) {
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
