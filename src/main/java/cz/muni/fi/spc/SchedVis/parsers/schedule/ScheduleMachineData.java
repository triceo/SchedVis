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
 * This holds information about jobs related to a given machine in one event.
 * Filled by parser, read by importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class ScheduleMachineData {

	private final char[] machineId;
	private final List<ScheduleJobData> jobs;

	public ScheduleMachineData(final Token machineId,
	    final List<ScheduleJobData> data) {
		this.jobs = data;
		this.machineId = machineId.toString().toCharArray();
	}

	public List<ScheduleJobData> getJobs() {
		return this.jobs;
	}

	public String getMachineId() {
		return String.valueOf(this.machineId);
	}
}
