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
import java.util.Formatter;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.EventType;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

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

	private static String parseTime(double seconds) {
		final int secondsInAMinute = 60;
		final int secondsInAnHour = secondsInAMinute * 60;
		final int secondsInADay = secondsInAnHour * 24;
		// how many days
		final int days = Double.valueOf(Math.floor(seconds / secondsInADay))
		    .intValue();
		seconds -= days * secondsInADay;
		// how many hours
		final int hours = Double.valueOf(Math.floor(seconds / secondsInAnHour))
		    .intValue();
		seconds -= hours * secondsInAnHour;
		// how many Minutes
		final int minutes = Double.valueOf(Math.floor(seconds / secondsInAMinute))
		    .intValue();
		seconds -= minutes * secondsInAMinute;
		return new Formatter().format(Messages.getString("StatusBar.0"),
		    new Object[] { days, hours, minutes, (int) seconds }).toString();
	}

	private StatusBar() {
		super();
		this.updateFrame(Event.getFirst());
	}

	public void updateFrame(final Event event) {
		EventType eventType;
		String text = Messages.getString("StatusBar.1");
		try {
			eventType = event.getType();
		} catch (final NullPointerException e) {
			eventType = null;
		}
		if (eventType != null) {
			if (eventType == EventType.JOB_ARRIVAL) {
				// job arrived
				text = new Formatter().format(Messages.getString("StatusBar.2"),
				    event.getJob()).toString();
			} else if (eventType == EventType.JOB_COMPLETION) {
				// job completed
				text = new Formatter().format(Messages.getString("StatusBar.3"),
				    event.getJob()).toString();
			} else if (eventType == EventType.JOB_CANCEL) {
				// job cancelled
				text = new Formatter().format(Messages.getString("StatusBar.4"),
				    event.getJob()).toString();
			} else if (eventType == EventType.JOB_EXECUTION_START) {
				// job started executing
				text = new Formatter().format(Messages.getString("StatusBar.5"),
				    event.getJob()).toString();
			} else if (eventType == EventType.MACHINE_RESTART) {
				// machine restart
				text = new Formatter().format(Messages.getString("StatusBar.6"),
				    event.getSourceMachine()).toString();
			} else if ((eventType == EventType.MACHINE_FAIL)
			    || (eventType == EventType.MACHINE_FAIL_MOVE_GOOD)
			    || (eventType == EventType.MACHINE_FAIL_MOVE_BAD)) {
				// machine failure
				text = new Formatter().format(Messages.getString("StatusBar.7"),
				    event.getSourceMachine()).toString();
			}
			if ((eventType == EventType.MOVE_GOOD)
			    || (eventType == EventType.MACHINE_FAIL_MOVE_GOOD)) {
				// good move
				text = new Formatter().format(
				    Messages.getString("StatusBar.8"),
				    new Object[] { event.getJob(), event.getSourceMachine().getName(),
				        event.getTargetMachine().getName() }).toString();
			} else if ((eventType == EventType.MOVE_BAD)
			    || (eventType == EventType.MACHINE_FAIL_MOVE_BAD)) {
				text = new Formatter().format(
				    Messages.getString("StatusBar.9"),
				    new Object[] { event.getJob(), event.getSourceMachine().getName(),
				        event.getTargetMachine().getName() }).toString();
			}
		}
		this.removeAll();
		final BorderLayout brdr = new BorderLayout();
		this.setLayout(brdr);
		this.add(new JLabel(new Formatter().format(
		    Messages.getString("StatusBar.10"),
		    new Object[] { event.getId(), StatusBar.parseTime(event.getClock()),
		        text }).toString()), BorderLayout.CENTER);
		this.updateUI();
	}
}
