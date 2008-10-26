/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import java.util.List;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleEventIO extends ScheduleEvent implements EventHasData,
		EventIsJobRelated {

	private final Integer										jobId;
	private final List<ScheduleMachineData>	data;

	public ScheduleEventIO(final Token event, final Token clock,
			final Token jobId, final List<ScheduleMachineData> data) {
		super(event, clock);
		this.jobId = new Integer(jobId.toString());
		this.data = data;
	}

	public List<ScheduleMachineData> getData() {
		return this.data;
	}

	public Integer getJob() {
		return this.jobId;
	}

}
