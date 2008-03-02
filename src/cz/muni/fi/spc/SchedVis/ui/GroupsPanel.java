/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JCheckBox;

import cz.muni.fi.spc.SchedVis.model.GroupEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupsPanel extends JBorderedPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8820656296600471333L;

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

	private void specialize() {
		this.setLayout(new GridLayout(0, 2));
		this.update();
	}

	public void update() {
		this.removeAll();
		try {
			final ResultSet rs = GroupEntity.getAllGroups();
			while (rs.next()) {
				this.add(new JCheckBox(rs.getString("name")));
			}
		} catch (final SQLException e) {
			// do nothing
		}
	}

}
