/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

/**
 * This holds all information about a single event. Filled by parser, read by
 * importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public class ScheduleEvent {
	
	private String event;
	private Integer clock;
	
	public ScheduleEvent(Token event, Token clock) {
		this.event = event.toString();
		this.clock = new Integer(clock.toString());
	}
	
	public Integer getClock() {
		return this.clock;
	}
	
	public String getName() {
		return this.event;
	}

}
