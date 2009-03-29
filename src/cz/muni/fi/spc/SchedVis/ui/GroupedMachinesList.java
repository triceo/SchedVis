/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

import cz.muni.fi.spc.SchedVis.model.models.MachinesListModel;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupedMachinesList extends JList {

	/**
     * 
     */
	private static final long serialVersionUID = 157627105166533902L;

	/**
     * 
     */
	public GroupedMachinesList(final Integer groupId,
	    final ListSelectionListener owner) {
		this.setModel(new MachinesListModel(groupId, null));
		this.update();
		this.addListSelectionListener(owner);
	}

	public void update() {
		final MachinesListModel model = (MachinesListModel) this.getModel();
		model.update();
	}
}
