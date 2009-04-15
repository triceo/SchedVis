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
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import cz.muni.fi.spc.SchedVis.parsers.Token;

/**
 * This holds all information about a single event. Filled by parser, read by
 * importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public abstract class ScheduleEvent {

	private final char[] event;
	private final int clock;

	public ScheduleEvent(final Token event, final Token clock) {
		this.event = event.toString().toCharArray();
		this.clock = Integer.valueOf(clock.toString()).intValue();
	}

	/**
	 * Call after the data are processed so that we can clear some memory.
	 */
	public abstract void clear();

	public int getClock() {
		return this.clock;
	}

	public String getName() {
		return String.valueOf(this.event);
	}

}
