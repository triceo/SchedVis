package cz.muni.fi.spc.SchedVis.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cz.muni.fi.spc.SchedVis.Main;
import cz.muni.fi.spc.SchedVis.model.entities.Group;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.models.GroupsListModel;
import cz.muni.fi.spc.SchedVis.model.models.MachinesListModel;
import cz.muni.fi.spc.SchedVis.ui.GroupedMachinesList;

/**
 * Creates the dialog to create and/or update machine groups.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Groups extends JDialog implements ActionListener,
		ListDataListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1070355363275232038L;

	private JTextField newGroupName;
	private JButton newGroupButton;
	private JButton deleteGroupButton;
	private JButton addMachineButton;
	private JButton removeMachineButton;
	private JButton closeButton;
	private JComboBox availableGroupsList;
	private GroupedMachinesList availableMachinesList;
	private GroupedMachinesList groupedMachinesList;

	private final String COMMAND__CLOSE_DIALOG = "closeDialog";
	private final String COMMAND__CREATE_NEW_GROUP = "createNewGroup";
	private final String COMMAND__DELETE_GROUP = "deleteGroup";
	private final String COMMAND__ADD_MACHINE_TO_GROUP = "addToGroup";
	private final String COMMAND__REMOVE_MACHINE_FROM_GROUP = "removeFromGroup";

	/**
	 * 
	 */
	public Groups() {
		super();
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public Groups(final Dialog owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public Groups(final Dialog owner, final boolean modal) {
		super(owner, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public Groups(final Dialog owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public Groups(final Dialog owner, final String title, final boolean modal) {
		super(owner, title, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public Groups(final Dialog owner, final String title, final boolean modal,
			final GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public Groups(final Frame owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public Groups(final Frame owner, final boolean modal) {
		super(owner, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public Groups(final Frame owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public Groups(final Frame owner, final String title, final boolean modal) {
		super(owner, title, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public Groups(final Frame owner, final String title, final boolean modal,
			final GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public Groups(final Window owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modalityType
	 */
	public Groups(final Window owner, final ModalityType modalityType) {
		super(owner, modalityType);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public Groups(final Window owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public Groups(final Window owner, final String title,
			final ModalityType modalityType) {
		super(owner, title, modalityType);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 * @param gc
	 */
	public Groups(final Window owner, final String title,
			final ModalityType modalityType, final GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		this.specialize();
	}

	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals(this.COMMAND__CREATE_NEW_GROUP)) {
			final String text = this.newGroupName.getText().trim();
			if (text.length() == 0) {
				JOptionPane.showMessageDialog(this,
						"Please provide a name for the group.");
			} else if (Group.insert(this.newGroupName.getText())) {
				final GroupsListModel model = (GroupsListModel) this.availableGroupsList
						.getModel();
				model.update();
				this.availableGroupsList.setSelectedItem(text);
				this.newGroupName.setText("");
			} else {
				JOptionPane.showMessageDialog(this,
						"Cannot create a group. Probably already exists.");
			}
		} else if (command.equals(this.COMMAND__DELETE_GROUP)) {
			final Integer id = Group
					.getIdWithName((String) this.availableGroupsList
							.getSelectedItem());
			if (Group.delete(id)) {
				final GroupsListModel model = (GroupsListModel) this.availableGroupsList
						.getModel();
				model.update();
				this.availableMachinesList.update();
				if (model.getSize() == 0) {
					this.deleteGroupButton.setEnabled(false);
					this.groupedMachinesList.setModel(new MachinesListModel(-1,
							this));
					this.groupedMachinesList.setEnabled(false);
					this.availableMachinesList.setEnabled(false);
					this.addMachineButton.setEnabled(false);
					this.removeMachineButton.setEnabled(false);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Cannot delete a group.");
			}
		} else if (command.equals(this.COMMAND__ADD_MACHINE_TO_GROUP)) {
			for (final Object machineName : this.availableMachinesList
					.getSelectedValues()) {
				final Integer groupId = Group
						.getIdWithName((String) this.availableGroupsList
								.getSelectedItem());
				Machine.addToGroup((String) machineName, groupId);
				this.availableMachinesList.update();
				this.groupedMachinesList.update();
			}
		} else if (command.equals(this.COMMAND__REMOVE_MACHINE_FROM_GROUP)) {
			for (final Object machineName : this.groupedMachinesList
					.getSelectedValues()) {
				Machine.removeFromGroup((String) machineName);
				this.availableMachinesList.update();
				this.groupedMachinesList.update();
			}
		} else if (command.equals(this.COMMAND__CLOSE_DIALOG)) {
			this.setVisible(false);
		}
		Main.update();
	}

	public void contentsChanged(final ListDataEvent e) {
		final Object source = e.getSource();
		if (source.equals(this.availableGroupsList.getModel())) {
			// caught group selection
			if ((this.availableGroupsList.getSelectedIndex() == -1)) {
				this.deleteGroupButton.setEnabled(false);
				this.groupedMachinesList.setEnabled(false);
				this.availableMachinesList.setEnabled(false);
			} else {
				final String name = (String) this.availableGroupsList
						.getSelectedItem();
				final Integer groupId = Group.getIdWithName(name);
				this.deleteGroupButton.setEnabled(true);
				this.groupedMachinesList.setEnabled(true);
				this.availableMachinesList.setEnabled(true);
				this.groupedMachinesList.setModel(new MachinesListModel(
						groupId, this));
			}
		}
	}

	public void intervalAdded(final ListDataEvent e) {
		// intentionally blank
	}

	public void intervalRemoved(final ListDataEvent e) {
		// intentionally blank
	}

	private void specialize() {
		this.setMinimumSize(new Dimension(650, 400));
		this.setTitle("Manage machine groups");
		final JPanel topPane = new JPanel();
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.LINE_AXIS));
		// add group-pickling pane
		final JPanel topLeftPane = new JPanel();
		this.availableGroupsList = new JComboBox();
		this.availableGroupsList.setModel(new GroupsListModel(this));
		final JLabel label = new JLabel("Group to modify:");
		label.setLabelFor(this.availableGroupsList);
		topLeftPane.add(label);
		topLeftPane.add(this.availableGroupsList);
		this.deleteGroupButton = new JButton("Delete");
		this.deleteGroupButton.setActionCommand(this.COMMAND__DELETE_GROUP);
		this.deleteGroupButton.addActionListener(this);
		this.deleteGroupButton.setEnabled(false);
		topLeftPane.add(this.deleteGroupButton);
		topPane.add(topLeftPane);
		// add group-creating pane
		final JPanel topRightPane = new JPanel();
		this.newGroupName = new JTextField(10);
		topRightPane.add(this.newGroupName);
		this.newGroupButton = new JButton("Create group");
		this.newGroupButton.setActionCommand(this.COMMAND__CREATE_NEW_GROUP);
		this.newGroupButton.addActionListener(this);
		topRightPane.add(this.newGroupButton);
		topPane.add(topRightPane);
		this.add(topPane, BorderLayout.PAGE_START);
		final JPanel bottomPane = new JPanel();
		// add machine-management panes
		final JPanel middlePane = new JPanel();
		final JPanel grpMachinesPane = new JPanel(); // panel with machines
		// in a group
		grpMachinesPane.setLayout(new BoxLayout(grpMachinesPane,
				BoxLayout.PAGE_AXIS));
		grpMachinesPane.add(new JLabel("Machines in the chosen group:"));
		this.groupedMachinesList = new GroupedMachinesList(-1, this);
		this.groupedMachinesList.setEnabled(false);
		grpMachinesPane.add(new JScrollPane(this.groupedMachinesList));
		middlePane.add(grpMachinesPane);
		this.add(middlePane);
		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.LINE_AXIS));
		final JPanel buttonsPane = new JPanel(); // panel with controls
		buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.PAGE_AXIS));
		this.addMachineButton = new JButton("<<");
		this.addMachineButton
				.setActionCommand(this.COMMAND__ADD_MACHINE_TO_GROUP);
		this.addMachineButton.setEnabled(false);
		this.addMachineButton.addActionListener(this);
		this.removeMachineButton = new JButton(">>");
		this.removeMachineButton.setEnabled(false);
		this.removeMachineButton
				.setActionCommand(this.COMMAND__REMOVE_MACHINE_FROM_GROUP);
		this.removeMachineButton.addActionListener(this);
		buttonsPane.add(this.addMachineButton);
		buttonsPane.add(this.removeMachineButton);
		middlePane.add(buttonsPane);
		final JPanel avlMachinesPane = new JPanel(); // panel with available
		// machines
		avlMachinesPane.setLayout(new BoxLayout(avlMachinesPane,
				BoxLayout.PAGE_AXIS));
		avlMachinesPane.add(new JLabel("Machines in no group:"));
		this.availableMachinesList = new GroupedMachinesList(null, this);
		this.availableMachinesList.setEnabled(false);
		avlMachinesPane.add(new JScrollPane(this.availableMachinesList));
		middlePane.add(avlMachinesPane);
		// add dialog-closing button
		bottomPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.closeButton = new JButton("Finish");
		this.closeButton.setActionCommand(this.COMMAND__CLOSE_DIALOG);
		this.closeButton.addActionListener(this);
		bottomPane.add(this.closeButton);
		this.add(bottomPane, BorderLayout.PAGE_END);
	}

	public void valueChanged(final ListSelectionEvent e) {
		final Object src = e.getSource();
		if (src.equals(this.availableMachinesList)) {
			if (this.availableMachinesList.getSelectedIndex() == -1) {
				this.addMachineButton.setEnabled(false);
			} else {
				this.addMachineButton.setEnabled(true);
			}
		} else if (src.equals(this.groupedMachinesList)) {
			if (this.groupedMachinesList.getSelectedIndex() == -1) {
				this.removeMachineButton.setEnabled(false);
			} else {
				this.removeMachineButton.setEnabled(true);
			}
		}
	}

}
