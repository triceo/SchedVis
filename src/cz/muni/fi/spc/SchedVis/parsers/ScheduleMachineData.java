/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

import java.util.List;

/**
 * This holds information about jobs related to a given machine in one event. 
 * Filled by parser, read by importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public class ScheduleMachineData {
	
	private String machineId;
	private List<ScheduleJobData> jobs;

	public ScheduleMachineData(Token machineId, List<ScheduleJobData> data) {
		this.jobs = data;
		this.machineId = machineId.toString();
	}
	
	public String getMachineId() {
		return this.machineId;
	}
	
	public List<ScheduleJobData> getJobs() {
		return this.jobs;
	}
}
