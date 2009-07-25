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
import javax.swing.JSplitPane;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;
import cz.muni.fi.spc.SchedVis.util.Database;
import cz.muni.fi.spc.SchedVis.util.ScheduleRenderingController;

/**
 * MainFrame class for SchedVis' user interface.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class MainFrame extends JFrame {

	/**
     * 
     */
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

		// Display the window.
		this.setMinimumSize(new Dimension(800, 600));
	}

	public Container createContentPane() {
		// get right panel
		final JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new BorderLayout());
		// get slider
		final JPanel sPanel = new SliderPanel();
		schedulePanel.add(sPanel, BorderLayout.PAGE_END);
		schedulePanel.setMinimumSize(sPanel.getPreferredSize());
		// get machine detail
		this.detailPane = new JBorderedPanel("Machine detail");
		this.updateDetail(null);
		// get scrolling pane with a tree
		final JPanel spanel = new JPanel();
		spanel.setLayout(new BorderLayout());
		final JScrollPane pane = new JScrollPane(spanel);
		spanel.add(this.detailPane, BorderLayout.PAGE_START);
		final JPanel schpanel = new JBorderedPanel("Complete schedule");
		schpanel.setLayout(new BorderLayout());
		schpanel.add(MainFrame.tree, BorderLayout.CENTER);
		spanel.add(schpanel, BorderLayout.CENTER);
		pane.setWheelScrollingEnabled(true);
		schedulePanel.add(pane, BorderLayout.CENTER);

		// get left panel
		final JPanel leftPanel = new JPanel();
		leftPanel.setMinimumSize(new Dimension(200, 200));
		// left stats sub-panel
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		final JPanel statsPanel = new JBorderedPanel("Just happened");
		statsPanel.setLayout(new BorderLayout());
		statsPanel.add(DescriptionPane.getInstance(), BorderLayout.CENTER);
		// get panel with group picker
		leftPanel.add(new JScrollPane(statsPanel));

		// Create a split pane with the two scroll panes in it.
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		    leftPanel, schedulePanel);
		splitPane.setOneTouchExpandable(true);
		return splitPane;
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
			final Event currentEvent = Database.getEntityManager().find(Event.class,
			    TimelineSliderModel.getInstance().getValue());
			final Integer previousEvent = Event.getPrevious(currentEvent, m).getId();
			final Integer nextEvent = Event.getNext(currentEvent, m).getId();
			for (final Integer event : new TreeSet<Integer>(Arrays
			    .asList(new Integer[] { previousEvent, currentEvent.getId(),
			        nextEvent }))) {
				ScheduleRenderingController.render(m, event);
				final MachinePanel pane = new MachinePanel();
				pane.setImage(ScheduleRenderingController.getRendered(m, event));
				this.detailPane.add(pane);
			}
		} else {
			this.detailPane.add(new JLabel(
			    "Click on any schedule and a machine detail will appear here."));
		}
		this.detailPane.updateUI();
	}
}