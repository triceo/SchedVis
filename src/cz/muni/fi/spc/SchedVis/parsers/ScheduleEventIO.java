/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

import java.util.List;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public class ScheduleEventIO extends ScheduleEvent {
	
	private Integer jobId;
	private List<ScheduleMachineData> data;
	
	public ScheduleEventIO(Token event, Token clock, Token jobId, List<ScheduleMachineData> data) {
		super(event, clock);
		this.jobId = new Integer(jobId.toString());
		this.data = data;
	}
	
	public Integer getJob() {
		return this.jobId;
	}
	
	public List<ScheduleMachineData> getData() {
		return this.data;
	}

}
