/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import javax.swing.JTree;

import cz.muni.fi.spc.SchedVis.model.models.ScheduleTreeModel;
import cz.muni.fi.spc.SchedVis.rendering.ScheduleTreeCellRenderer;

/**
 * The tree to display schedules in. It is a singleton, no more instances
 * needed.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleTree extends JTree {

	private static ScheduleTree tree = null;

	private static final long serialVersionUID = -5619978578879924763L;

	/**
	 * Get the only instance of the class.
	 * 
	 * @return The tree.
	 */
	public static ScheduleTree getInstance() {
		if (ScheduleTree.tree == null) {
			ScheduleTree.tree = new ScheduleTree();
		}
		return ScheduleTree.tree;
	}

	/**
	 * The constructor.
	 */
	private ScheduleTree() {
		super();
		this.setExpandsSelectedPaths(true);
		this.setShowsRootHandles(true);
		this.setCellRenderer(new ScheduleTreeCellRenderer());
		this.setEditable(false);
		this.setRootVisible(false);
		this.setOpaque(true);
		this.setModel(ScheduleTreeModel.getInstance());
	}
}
