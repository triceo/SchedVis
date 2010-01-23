package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
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

import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;
import cz.muni.fi.spc.SchedVis.model.models.GroupsListModel;
import cz.muni.fi.spc.SchedVis.model.models.MachinesListModel;
import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;
import cz.muni.fi.spc.SchedVis.util.Database;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

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
 * Creates the dialog to create and/or update machine groups.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class GroupsDialog extends JDialog implements ActionListener,
    ListDataListener, ListSelectionListener {

	/**
     * 
     */
	private static final long serialVersionUID = -1070355363275232038L;

	private final JTextField newGroupName;
	private final JButton newGroupButton;
	private final JButton deleteGroupButton;
	private final JButton addMachineButton;
	private final JButton removeMachineButton;
	private final JButton closeButton;
	private final JComboBox availableGroupsList;
	private final GroupedMachinesList availableMachinesList;
	private final GroupedMachinesList groupedMachinesList;

	private static final String COMMAND__CLOSE_DIALOG = "closeDialog";
	private static final String COMMAND__CREATE_NEW_GROUP = "createNewGroup";
	private static final String COMMAND__DELETE_GROUP = "deleteGroup";
	private static final String COMMAND__ADD_MACHINE_TO_GROUP = "addToGroup";
	private static final String COMMAND__REMOVE_MACHINE_FROM_GROUP = "removeFromGroup";

	/**
	 * Class constructor.
	 * 
	 * @param owner
	 *          The owner of the dialog. (An element to center on.)
	 * @param modal
	 *          Whether or not the dialog is modal.
	 */
	public GroupsDialog(final Frame owner, final boolean modal) {
		super(owner, modal);
		this.setTitle(Messages.getString("GroupsDialog.5"));
		final JPanel topPane = new JPanel();
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.LINE_AXIS));
		// add group-pickling pane
		final JPanel topLeftPane = new JPanel();
		this.availableGroupsList = new JComboBox();
		this.availableGroupsList.setModel(new GroupsListModel(this));
		final JLabel label = new JLabel(Messages.getString("GroupsDialog.6"));
		label.setLabelFor(this.availableGroupsList);
		topLeftPane.add(label);
		topLeftPane.add(this.availableGroupsList);
		this.deleteGroupButton = new JButton(Messages.getString("GroupsDialog.7"));
		this.deleteGroupButton.setActionCommand(GroupsDialog.COMMAND__DELETE_GROUP);
		this.deleteGroupButton.addActionListener(this);
		this.deleteGroupButton.setEnabled(false);
		topLeftPane.add(this.deleteGroupButton);
		topPane.add(topLeftPane);
		// add group-creating pane
		final JPanel topRightPane = new JPanel();
		this.newGroupName = new JTextField(10);
		topRightPane.add(this.newGroupName);
		this.newGroupButton = new JButton(Messages.getString("GroupsDialog.8"));
		this.newGroupButton
		    .setActionCommand(GroupsDialog.COMMAND__CREATE_NEW_GROUP);
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
		grpMachinesPane.add(new JLabel(Messages.getString("GroupsDialog.9")));
		this.groupedMachinesList = new GroupedMachinesList(null, this);
		this.groupedMachinesList.setEnabled(false);
		grpMachinesPane.add(new JScrollPane(this.groupedMachinesList));
		middlePane.add(grpMachinesPane);
		this.add(middlePane);
		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.LINE_AXIS));
		final JPanel buttonsPane = new JPanel(); // panel with controls
		buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.PAGE_AXIS));
		this.addMachineButton = new JButton("<<");
		this.addMachineButton
		    .setActionCommand(GroupsDialog.COMMAND__ADD_MACHINE_TO_GROUP);
		this.addMachineButton.setEnabled(false);
		this.addMachineButton.addActionListener(this);
		this.removeMachineButton = new JButton(">>");
		this.removeMachineButton.setEnabled(false);
		this.removeMachineButton
		    .setActionCommand(GroupsDialog.COMMAND__REMOVE_MACHINE_FROM_GROUP);
		this.removeMachineButton.addActionListener(this);
		buttonsPane.add(this.addMachineButton);
		buttonsPane.add(this.removeMachineButton);
		middlePane.add(buttonsPane);
		final JPanel avlMachinesPane = new JPanel(); // panel with available
		// machines
		avlMachinesPane.setLayout(new BoxLayout(avlMachinesPane,
		    BoxLayout.PAGE_AXIS));
		avlMachinesPane.add(new JLabel(Messages.getString("GroupsDialog.12")));
		this.availableMachinesList = new GroupedMachinesList(null, this);
		this.availableMachinesList.setEnabled(false);
		avlMachinesPane.add(new JScrollPane(this.availableMachinesList));
		middlePane.add(avlMachinesPane);
		// add dialog-closing button
		bottomPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.closeButton = new JButton(Messages.getString("GroupsDialog.13"));
		this.closeButton.setActionCommand(GroupsDialog.COMMAND__CLOSE_DIALOG);
		this.closeButton.addActionListener(this);
		bottomPane.add(this.closeButton);
		this.add(bottomPane, BorderLayout.PAGE_END);
	}

	/**
	 * Listens to events on buttons in the dialog.
	 */
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals(GroupsDialog.COMMAND__CREATE_NEW_GROUP)) {
			final String text = this.newGroupName.getText().trim();
			if (text.length() == 0) {
				JOptionPane.showMessageDialog(this, Messages
				    .getString("GroupsDialog.14"));
			} else {
				if (MachineGroup.getWithName(this.newGroupName.getText(), false) == null) {
					final MachineGroup entity = new MachineGroup();
					entity.setName(this.newGroupName.getText());
					Database.persist(entity);
					final GroupsListModel model = (GroupsListModel) this.availableGroupsList
					    .getModel();
					model.update();
					this.availableGroupsList.setSelectedItem(text);
					this.newGroupName.setText("");
				} else {
					JOptionPane.showMessageDialog(this, Messages
					    .getString("GroupsDialog.16"));
				}
			}
		} else if (command.equals(GroupsDialog.COMMAND__DELETE_GROUP)) {
			final MachineGroup mg = MachineGroup.getWithName(
			    (String) this.availableGroupsList.getSelectedItem(), false);
			for (final Machine m : Machine.getAll(mg.getId())) {
				m.setGroup(null);
				Database.merge(m);
			}
			Database.remove(mg);
			if (!Database.getEntityManager().contains(mg)) {
				final GroupsListModel model = (GroupsListModel) this.availableGroupsList
				    .getModel();
				model.update();
				this.availableMachinesList.update();
				if (model.getSize() == 0) {
					this.deleteGroupButton.setEnabled(false);
					this.groupedMachinesList.setModel(new MachinesListModel(-1, this));
					this.groupedMachinesList.setEnabled(false);
					this.availableMachinesList.setEnabled(false);
					this.addMachineButton.setEnabled(false);
					this.removeMachineButton.setEnabled(false);
				}
			} else {
				JOptionPane.showMessageDialog(this, Messages
				    .getString("GroupsDialog.17"));
			}
		} else if (command.equals(GroupsDialog.COMMAND__ADD_MACHINE_TO_GROUP)) {
			final MachineGroup ge = MachineGroup.getWithName(
			    ((String) this.availableGroupsList.getSelectedItem()), false);
			for (final Object machineName : this.availableMachinesList
			    .getSelectedValues()) {
				final Machine m = Machine.getWithName((String) machineName, false);
				m.setGroup(ge);
				Database.merge(m);
			}
		} else if (command.equals(GroupsDialog.COMMAND__REMOVE_MACHINE_FROM_GROUP)) {
			for (final Object machineName : this.groupedMachinesList
			    .getSelectedValues()) {
				final Machine me = Machine.getWithName((String) machineName, false);
				me.setGroup(null);
				Database.merge(me);
			}
		} else if (command.equals(GroupsDialog.COMMAND__CLOSE_DIALOG)) {
			this.setVisible(false);
			// to prevent various out-of-date problems later in the UI
			Database.merge(MachineGroup.getAll());
			ScheduleTreeModel.getInstance().regroup();
		}
		this.availableMachinesList.update();
		this.groupedMachinesList.update();
	}

	/**
	 * Listens on changes in contents of lists in the dialog.
	 */
	public void contentsChanged(final ListDataEvent e) {
		final Object source = e.getSource();
		if (source.equals(this.availableGroupsList.getModel())) {
			// caught group selection
			if ((this.availableGroupsList.getSelectedIndex() < 1)) {
				this.deleteGroupButton.setEnabled(false);
				this.groupedMachinesList.setEnabled(false);
				this.availableMachinesList.setEnabled(false);
			} else {
				final String name = (String) this.availableGroupsList.getSelectedItem();
				final MachineGroup ge = MachineGroup.getWithName(name, true);
				this.deleteGroupButton.setEnabled(true);
				this.groupedMachinesList.setEnabled(true);
				this.availableMachinesList.setEnabled(true);
				this.groupedMachinesList.setModel(new MachinesListModel(ge.getId(),
				    this));
			}
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(650, 400);
	}

	public void intervalAdded(final ListDataEvent e) {
		// intentionally blank
	}

	public void intervalRemoved(final ListDataEvent e) {
		// intentionally blank
	}

	/**
	 * Listens to changes in selection of lists in this dialog.
	 */
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
