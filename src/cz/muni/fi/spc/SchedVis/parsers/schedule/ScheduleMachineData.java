/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import java.util.List;

/**
 * This holds information about jobs related to a given machine in one event.
 * Filled by parser, read by importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleMachineData {

	private final String								machineId;
	private final List<ScheduleJobData>	jobs;

	public ScheduleMachineData(final Token machineId,
			final List<ScheduleJobData> data) {
		this.jobs = data;
		this.machineId = machineId.toString();
	}

	public List<ScheduleJobData> getJobs() {
		return this.jobs;
	}

	public String getMachineId() {
		return this.machineId;
	}
}
