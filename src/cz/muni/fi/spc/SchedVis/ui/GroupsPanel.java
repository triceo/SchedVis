/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JCheckBox;

import cz.muni.fi.spc.SchedVis.model.entities.GroupEntity;
import cz.muni.fi.spc.SchedVis.ui.common.JBorderedPanel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupsPanel extends JBorderedPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8820656296600471333L;

	private final AbstractMap<Integer, JCheckBox> boxes = new HashMap<Integer, JCheckBox>();

	/**
	 * @param isDoubleBuffered
	 * @param title
	 */
	public GroupsPanel(final boolean isDoubleBuffered, final String title) {
		super(isDoubleBuffered, title);
		this.specialize();
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 * @param title
	 */
	public GroupsPanel(final LayoutManager layout,
			final boolean isDoubleBuffered, final String title) {
		super(layout, isDoubleBuffered, title);
		this.specialize();
	}

	/**
	 * @param layout
	 * @param title
	 */
	public GroupsPanel(final LayoutManager layout, final String title) {
		super(layout, title);
		this.specialize();
	}

	/**
	 * @param title
	 */
	public GroupsPanel(final String title) {
		super(title);
		this.specialize();
	}

	public AbstractSet<Integer> getSelectedGroups() {
		final AbstractSet<Integer> selectedBoxes = new HashSet<Integer>();
		for (final Integer groupId : this.boxes.keySet()) {
			if (this.boxes.get(groupId).isSelected()) {
				selectedBoxes.add(groupId);
			}
		}
		return selectedBoxes;
	}

	private void specialize() {
		this.setLayout(new GridLayout(0, 2));
		this.update();
	}

	public void update() {
		final AbstractSet<Integer> selectedGroups = this.getSelectedGroups();
		// assemble new groups
		try {
			final ResultSet rs = GroupEntity.getAllGroups();
			this.boxes.clear();
			while (rs.next()) {
				this.boxes.put(rs.getInt("id_machine_groups"), new JCheckBox(rs
						.getString("name")));
			}
			this.boxes.put(-1, new JCheckBox("Ungrouped"));
		} catch (final SQLException e) {
			// do nothing
		}
		// update the widget with new groups
		this.removeAll();
		for (final Integer groupId : this.boxes.keySet()) {
			final JCheckBox box = this.boxes.get(groupId);
			// select those selected before
			if (selectedGroups.contains(groupId)) {
				box.setSelected(true);
			}
			// add it to the screen
			this.add(box);
		}
	}

}
