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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * MainFrame class for SchedVis' user interface.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MainFrame extends JFrame {

	private static final long serialVersionUID = 6652856626507094021L;
	private static ScheduleTree tree = ScheduleTree.getInstance();
	private JPanel detailPane;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public MainFrame() {
		// Create and set up the window.
		this.setTitle("SchedVis");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		this.setJMenuBar(new MainMenu(this));
		final Container pane = this.createContentPane();
		this.setContentPane(pane);
		this.setMinimumSize(new Dimension(800, 600));
	}

	public Container createContentPane() {
		final JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new BorderLayout());
		// get machine detail
		this.detailPane = new JBorderedPanel(Messages.getString("MainFrame.1"));
		this.updateDetail(null);
		// get scrolling pane with a tree
		final JPanel spanel = new JPanel();
		spanel.setLayout(new BorderLayout());
		final JScrollPane pane = new JScrollPane(spanel);
		spanel.add(this.detailPane, BorderLayout.PAGE_START);
		final JPanel schpanel = new JBorderedPanel(Messages
		    .getString("MainFrame.2"));
		schpanel.setLayout(new BorderLayout());
		schpanel.add(MainFrame.tree, BorderLayout.CENTER);
		spanel.add(schpanel, BorderLayout.CENTER);
		pane.setWheelScrollingEnabled(true);
		schedulePanel.add(pane, BorderLayout.CENTER);
		// get the bottom pane
		final JPanel sPanel = new SliderPanel();
		final JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
		bottom.add(sPanel);
		bottom.add(StatusBar.getInstance());
		schedulePanel.add(bottom, BorderLayout.PAGE_END);
		return schedulePanel;
	}

	/**
	 * Updates the detail panel with the data about a given machine.
	 * 
	 * @param m
	 *          The machine.
	 */
	public void updateDetail(final Machine m) {
		this.detailPane.removeAll();
		if (m != null) {
			this.detailPane.setLayout(new BoxLayout(this.detailPane,
			    BoxLayout.PAGE_AXIS));
			final Event currentEvent = TimelineSliderModel.getInstance()
			    .getRichValue();
			final Event previousEvent = Event.getPrevious(currentEvent, m);
			final Event nextEvent = Event.getNext(currentEvent, m);
			for (final Event event : new TreeSet<Event>(Arrays.asList(new Event[] {
			    previousEvent, currentEvent, nextEvent }))) {
				this.detailPane.add(new MachinePanel(m, event));
			}
		} else {
			this.detailPane.add(new JLabel(Messages.getString("MainFrame.3")));
		}
		this.detailPane.updateUI();
	}
}