/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.EventType;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class StatusBar extends JPanel {

	private static StatusBar instance = null;

	/**
   * 
   */
	private static final long serialVersionUID = 5695795609101699931L;

	public static StatusBar getInstance() {
		if (StatusBar.instance == null) {
			StatusBar.instance = new StatusBar();
		}
		return StatusBar.instance;
	}

	private static String parseTime(Double seconds) {
		final Integer secondsInAMinute = 60;
		final Integer secondsInAnHour = secondsInAMinute * 60;
		final Integer secondsInADay = secondsInAnHour * 24;
		// how many days
		final Integer days = Double.valueOf(Math.floor(seconds / secondsInADay))
		    .intValue();
		seconds -= days * secondsInADay;
		// how many hours
		final Integer hours = Double.valueOf(Math.floor(seconds / secondsInAnHour))
		    .intValue();
		seconds -= hours * secondsInAnHour;
		// how many Minutes
		final Integer minutes = Double.valueOf(
		    Math.floor(seconds / secondsInAMinute)).intValue();
		seconds -= minutes * secondsInAMinute;
		return days + "d " + hours + "h " + minutes + "m " + seconds.intValue()
		    + "s";
	}

	private StatusBar() {
		super();
		this.updateFrame(Event.getFirst());
	}

	public void updateFrame(final Event event) {
		Integer eventType;
		String text = "";
		try {
			eventType = event.getType().getId();
		} catch (final NullPointerException e) {
			eventType = null;
		}
		if (eventType != null) {
			if (eventType.equals(EventType.EVENT_JOB_ARRIVAL)) {
				// job arrived
				text = "Job " + event.getJob() + " just arrived.";
			} else if (eventType.equals(EventType.EVENT_JOB_COMPLETION)) {
				// job completed
				text = "Job " + event.getJob() + " just completed.";
			} else if (eventType.equals(EventType.EVENT_JOB_CANCEL)) {
				// job cancelled
				text = "Job " + event.getJob() + " just cancelled.";
			} else if (eventType.equals(EventType.EVENT_JOB_EXECUTION_START)) {
				// job started executing
				text = "Job " + event.getJob() + " just started executing.";
			} else if (eventType.equals(EventType.EVENT_MACHINE_RESTART)) {
				// machine restart
				text = "Machine " + event.getSourceMachine()
				    + " just came back online.";
			} else if (eventType.equals(EventType.EVENT_MACHINE_FAILURE)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
				// machine failure
				text = "Machine " + event.getSourceMachine() + " just failed.";
			}
			if (eventType.equals(EventType.EVENT_JOB_MOVE_GOOD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)) {
				// good move
				text = "Job " + event.getJob() + " correctly moved from machine "
				    + event.getSourceMachine().getName() + " to machine "
				    + event.getSourceMachine().getName() + ".";
			} else if (eventType.equals(EventType.EVENT_JOB_MOVE_BAD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
				text = "Job " + event.getJob() + " incorrectly moved from machine "
				    + event.getSourceMachine().getName() + " to machine "
				    + event.getSourceMachine().getName() + ".";
			}
		} else {
			text = "Idle.";
		}
		this.removeAll();
		final BorderLayout brdr = new BorderLayout();
		this.setLayout(brdr);
		this.add(new JLabel("Event #" + event.getJob() + ", elapsed time "
		    + StatusBar.parseTime(event.getClock().doubleValue()) + " -- " + text),
		    BorderLayout.CENTER);
		this.updateUI();
	}
}
