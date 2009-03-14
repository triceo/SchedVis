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
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class GroupsListModel extends DefaultComboBoxModel {

    /**
     * 
     */
    private static final long serialVersionUID = -3334519146045362529L;

    /**
     * 
     */
    public GroupsListModel(final ListDataListener listener) {
	this.update();
	this.addListDataListener(listener);
    }

    public void update() {
	this.removeAllElements();
	this.addElement("No group");
	for (final MachineGroup item : MachineGroup.getAll()) {
	    this.addElement(item.getName());
	}
    }

}
