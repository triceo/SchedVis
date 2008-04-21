/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

import java.util.List;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public class ScheduleEventMove extends ScheduleEvent {
	
	private Integer jobId;
	private String srcMachine;
	private String dstMachine;
	private List<ScheduleMachineData> data;
	
	public ScheduleEventMove(Token event, Token clock, Token jobId, Token srcMachine, Token dstMachine, List<ScheduleMachineData> data) {
		super(event, clock);
		this.jobId = new Integer(jobId.toString());
		this.srcMachine = srcMachine.toString();
		this.dstMachine = dstMachine.toString();
		this.data = data;
	}
	
	public String getSourceMachine() {
		return this.srcMachine;
	}
	
	public String getTargetMachine() {
		return this.dstMachine;
	}

	public Integer getJob() {
		return this.jobId;
	}
	
	public List<ScheduleMachineData> getData() {
		return this.data;
	}

}
