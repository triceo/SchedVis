/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.schedule;

/**
 * This holds all information about a single event. Filled by parser, read by
 * importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ScheduleEvent {

    private final String event;
    private final Integer clock;

    public ScheduleEvent(final Token event, final Token clock) {
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
