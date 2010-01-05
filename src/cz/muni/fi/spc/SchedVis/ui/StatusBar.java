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
import cz.muni.fi.spc.SchedVis.util.PrintfFormat;
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
		return new PrintfFormat(Messages.getString("StatusBar.0")) //$NON-NLS-1$
		    .sprintf(new Integer[] { days, hours, minutes, (int) seconds });
	}

	private StatusBar() {
		super();
		this.updateFrame(Event.getFirst());
	}

	public void updateFrame(final Event event) {
		Integer eventType;
		String text = Messages.getString("StatusBar.1"); //$NON-NLS-1$
		try {
			eventType = event.getType().getId();
		} catch (final NullPointerException e) {
			eventType = null;
		}
		if (eventType != null) {
			if (eventType.equals(EventType.EVENT_JOB_ARRIVAL)) {
				// job arrived
				text = new PrintfFormat(Messages.getString("StatusBar.2")) //$NON-NLS-1$
				    .sprintf(event.getJob());
			} else if (eventType.equals(EventType.EVENT_JOB_COMPLETION)) {
				// job completed
				text = new PrintfFormat(Messages.getString("StatusBar.3")).sprintf(event //$NON-NLS-1$
				        .getJob());
			} else if (eventType.equals(EventType.EVENT_JOB_CANCEL)) {
				// job cancelled
				text = new PrintfFormat(Messages.getString("StatusBar.4")).sprintf(event //$NON-NLS-1$
				        .getJob());
			} else if (eventType.equals(EventType.EVENT_JOB_EXECUTION_START)) {
				// job started executing
				text = new PrintfFormat(Messages.getString("StatusBar.5")) //$NON-NLS-1$
				    .sprintf(event.getJob());
			} else if (eventType.equals(EventType.EVENT_MACHINE_RESTART)) {
				// machine restart
				text = new PrintfFormat(Messages.getString("StatusBar.6")) //$NON-NLS-1$
				    .sprintf(event.getSourceMachine());
			} else if (eventType.equals(EventType.EVENT_MACHINE_FAILURE)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
				// machine failure
				text = new PrintfFormat(Messages.getString("StatusBar.7")).sprintf(event //$NON-NLS-1$
				        .getSourceMachine());
			}
			if (eventType.equals(EventType.EVENT_JOB_MOVE_GOOD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)) {
				// good move
				text = new PrintfFormat(Messages.getString("StatusBar.8")) //$NON-NLS-1$
				    .sprintf(new Object[] { event.getJob(),
				        event.getSourceMachine().getName(),
				        event.getTargetMachine().getName() });
			} else if (eventType.equals(EventType.EVENT_JOB_MOVE_BAD)
			    || eventType.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
				text = new PrintfFormat(Messages.getString("StatusBar.9")) //$NON-NLS-1$
				    .sprintf(new Object[] { event.getJob(),
				        event.getSourceMachine().getName(),
				        event.getTargetMachine().getName() });
			}
		}
		this.removeAll();
		final BorderLayout brdr = new BorderLayout();
		this.setLayout(brdr);
		this.add(new JLabel(new PrintfFormat(Messages.getString("StatusBar.10")) //$NON-NLS-1$
		    .sprintf(new Object[] { event.getJob(),
		        StatusBar.parseTime(event.getClock()), text })),
		    BorderLayout.CENTER);
		this.updateUI();
	}
}
