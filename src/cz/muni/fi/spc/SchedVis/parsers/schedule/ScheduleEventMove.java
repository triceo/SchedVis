/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import java.util.List;

import cz.muni.fi.spc.SchedVis.parsers.schedule.Token;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleEventMove extends ScheduleEvent implements EventHasData,
		EventIsJobRelated, EventIsMachineRelated {

	private final Integer jobId;
	private final String srcMachine;
	private final String dstMachine;
	private final List<ScheduleMachineData> data;

	public ScheduleEventMove(final Token event, final Token clock,
			final Token jobId, final Token srcMachine, final Token dstMachine,
			final List<ScheduleMachineData> data) {
		super(event, clock);
		this.jobId = new Integer(jobId.toString());
		this.srcMachine = srcMachine.toString();
		this.dstMachine = dstMachine.toString();
		this.data = data;
	}

	public List<ScheduleMachineData> getData() {
		return this.data;
	}

	public Integer getJob() {
		return this.jobId;
	}

	public String getMachine() {
		return this.srcMachine;
	}

	public String getTargetMachine() {
		return this.dstMachine;
	}

}
