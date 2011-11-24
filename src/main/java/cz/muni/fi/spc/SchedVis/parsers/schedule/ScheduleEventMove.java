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

import java.util.List;

import cz.muni.fi.spc.SchedVis.parsers.Token;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleEventMove extends ScheduleEvent implements
    EventHasData, EventIsJobRelated, EventIsMachineRelated {

	private final int jobId;
	private final char[] srcMachine;
	private final char[] dstMachine;
	private final List<ScheduleMachineData> data;

	public ScheduleEventMove(final Token event, final Token clock,
	    final Token jobId, final Token srcMachine, final Token dstMachine,
	    final List<ScheduleMachineData> data) {
		super(event, clock);
		this.jobId = Integer.valueOf(jobId.toString()).intValue();
		this.srcMachine = srcMachine.toString().toCharArray();
		this.dstMachine = dstMachine.toString().toCharArray();
		this.data = data;
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	public List<ScheduleMachineData> getData() {
		return this.data;
	}

	public int getJob() {
		return this.jobId;
	}

	public String getMachine() {
		return String.valueOf(this.srcMachine);
	}

	public String getTargetMachine() {
		return String.valueOf(this.dstMachine);
	}

}
