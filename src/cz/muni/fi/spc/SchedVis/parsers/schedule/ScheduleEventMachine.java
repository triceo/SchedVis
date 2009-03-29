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
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import cz.muni.fi.spc.SchedVis.parsers.Token;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleEventMachine extends ScheduleEvent implements
    EventIsMachineRelated {

	private final String machineId;

	public ScheduleEventMachine(final Token event, final Token clock,
	    final Token machineId) {
		super(event, clock);
		this.machineId = machineId.toString();
	}

	public String getMachine() {
		return this.machineId;
	}

}
