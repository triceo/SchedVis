/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;

/**
 * MainWindow class for SchedVis' user interface.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MainWindow {

	private static GroupsPanel groupsPanel;

	private static JFrame frame;

	public static void main(final String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final MainWindow ui = new MainWindow();
				ui.createAndShowGUI();
			}
		});
	}

	public static void update() {
		MainWindow.groupsPanel.update();
		MainWindow.frame.pack();
		MainWindow.frame.repaint();
	}

	String newline = "\n";

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		MainWindow.frame = new JFrame("SchedVis");
		MainWindow.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// display a dialog
		final ImportDialog dialog = new ImportDialog(MainWindow.frame, true);
		dialog.setVisible(true);
		dialog.setVisible(false);

		// Create and set up the content pane.
		MainWindow.frame.setJMenuBar(new MainMenu(MainWindow.frame).get());
		final Container pane = this.createContentPane();
		MainWindow.frame.setContentPane(pane);

		// Display the window.
		MainWindow.frame.setMinimumSize(pane.getPreferredSize());
		MainWindow.frame.setVisible(true);
	}

	public Container createContentPane() {
		// get right panel
		final JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new BorderLayout());
		// get the tree
		schedulePanel.add(this.createSchedulePane(), BorderLayout.CENTER);
		// get slider
		final JPanel sPanel = new Timeline().get();
		// add it
		schedulePanel.add(sPanel, BorderLayout.PAGE_END);
		schedulePanel.setMinimumSize(sPanel.getPreferredSize());

		// get left panel
		final JPanel leftPanel = new JPanel();

		// left stats sub-panel
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		final JPanel statsPanel = new JBorderedPanel("Statistics");

		// left settings sub-panel
		final JPanel settingsPanel = new JBorderedPanel("Settings");
		settingsPanel.setLayout(new BoxLayout(settingsPanel,
				BoxLayout.PAGE_AXIS));
		// get panel with group picker
		MainWindow.groupsPanel = new GroupsPanel("Show following groups:");
		settingsPanel.add(MainWindow.groupsPanel);
		// get timescale panel
		final JPanel timePanel = new JBorderedPanel("Each step takes [s]:");
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.PAGE_AXIS));
		timePanel.add(new JSlider());
		timePanel.add(new JCheckBox("Break after every step."));
		settingsPanel.add(timePanel);

		settingsPanel.setMinimumSize(new Dimension(250, 0));

		leftPanel.add(statsPanel);
		leftPanel.add(settingsPanel);

		// Create a split pane with the two scroll panes in it.
		final JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, leftPanel, schedulePanel);
		splitPane.setOneTouchExpandable(true);
		return splitPane;
	}

	private Container createSchedulePane() {
		// get scrolling pane with a tree
		final JScrollPane pane = new JScrollPane(new JTree());
		pane.setWheelScrollingEnabled(true);
		// get regular pane for a machine detail
		final JPanel pane2 = new JPanel();
		// get a layout
		final JPanel layoutPane = new JPanel();
		layoutPane.setLayout(new BoxLayout(layoutPane, BoxLayout.PAGE_AXIS));
		layoutPane.add(pane);
		layoutPane.add(pane2);
		// adjust widths
		pane2.add(new JLabel("Here goes future machine detail."));
		return layoutPane;
	}

	// Returns just the class name -- no package info.
	protected String getClassName(final Object o) {
		final String classString = o.getClass().getName();
		final int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

}