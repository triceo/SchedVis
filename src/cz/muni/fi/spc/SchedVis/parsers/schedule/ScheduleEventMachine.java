/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleEventMachine extends ScheduleEvent implements
		EventIsMachineRelated {

	private final String	machineId;

	public ScheduleEventMachine(final Token event, final Token clock,
			final Token machineId) {
		super(event, clock);
		this.machineId = machineId.toString();
	}

	public String getMachine() {
		return this.machineId;
	}

}
