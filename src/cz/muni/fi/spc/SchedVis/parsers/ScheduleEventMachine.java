/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public class ScheduleEventMachine extends ScheduleEvent {
	
	private String machineId; 
	
	public ScheduleEventMachine(Token event, Token clock, Token machineId) {
		super(event, clock);
		this.machineId = machineId.toString();
	}
	
	public String getMachine() {
		return this.machineId;
	}

}
