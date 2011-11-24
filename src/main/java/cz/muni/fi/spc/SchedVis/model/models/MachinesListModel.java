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
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * Model for a list of machines in the groups dialog.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class MachinesListModel extends DefaultListModel {

	private static final long serialVersionUID = 7269134473621539118L;

	private final Integer groupId;

	/**
	 * Class constructor.
	 * 
	 * @param groupId
	 *          Machine group holding all the machines in this model. When null,
	 *          means "machines in no group."
	 * @param listener
	 *          What listens on changes to this model.
	 */
	public MachinesListModel(final Integer groupId,
	    final ListDataListener listener) {
		this.groupId = groupId;
		this.update();
		this.addListDataListener(listener);
	}

	/**
	 * Removes all items from the model and adds them back again.
	 */
	public void update() {
		this.removeAllElements();
		for (final Machine item : Machine.getAll(this.groupId)) {
			this.addElement(item.getName());
		}
	}

}
