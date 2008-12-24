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

import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;
import cz.muni.fi.spc.SchedVis.ui.common.JBorderedPanel;
import cz.muni.fi.spc.SchedVis.ui.common.JScheduleTree;
import cz.muni.fi.spc.SchedVis.ui.dialogs.Import;

/**
 * MainFrame class for SchedVis' user interface.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 6652856626507094021L;
    private static GroupsPanel groupsPanel;
    private static JScheduleTree tree = null;

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public MainFrame() {
	// Create and set up the window.
	this.setTitle("SchedVis");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// display a dialog
	final Import dialog = new Import(this, true);
	dialog.setVisible(true);
	dialog.setVisible(false);
	dialog.dispose();

	// Create and set up the content pane.
	this.setJMenuBar(new MainMenu(this));
	final Container pane = this.createContentPane();
	this.setContentPane(pane);

	// Display the window.
	this.setMinimumSize(pane.getPreferredSize());
    }

    public Container createContentPane() {
	// get right panel
	final JPanel schedulePanel = new JPanel();
	schedulePanel.setLayout(new BorderLayout());
	// get the tree
	schedulePanel.add(this.createSchedulePane(), BorderLayout.CENTER);

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
	MainFrame.groupsPanel = new GroupsPanel("Show following groups:");
	settingsPanel.add(MainFrame.groupsPanel);
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
	final JPanel layoutPane = new JPanel();
	layoutPane.setLayout(new BoxLayout(layoutPane, BoxLayout.PAGE_AXIS));
	// get slider
	final JPanel sPanel = new SliderPanel();
	// get scrolling pane with a tree
	MainFrame.tree = new JScheduleTree(ScheduleTreeModel.getInstance());
	final JScrollPane pane = new JScrollPane(MainFrame.tree);
	pane.setWheelScrollingEnabled(true);
	// get regular pane for a machine detail
	final JPanel pane2 = new JPanel();
	// get a layout
	layoutPane.add(pane);
	layoutPane.add(pane2);
	// adjust widths
	pane2.add(new JLabel("Here goes future machine detail."));
	// add slider
	layoutPane.add(sPanel, BorderLayout.PAGE_END);
	layoutPane.setMinimumSize(sPanel.getPreferredSize());
	return layoutPane;
    }

    public void update() {
	MainFrame.groupsPanel.update();
	this.pack();
	this.repaint();
    }

}