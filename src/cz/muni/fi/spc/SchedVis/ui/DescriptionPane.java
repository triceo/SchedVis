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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JEditorPane;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.EventType;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class DescriptionPane extends JEditorPane {

	private static String template = "";

	private static DescriptionPane instance = null;

	/**
   * 
   */
	private static final long serialVersionUID = 5695795609101699931L;

	public static DescriptionPane getInstance() {
		if (DescriptionPane.instance == null) {
			DescriptionPane.instance = new DescriptionPane();
		}
		return DescriptionPane.instance;
	}

	private static String setToString(final Set<String> set) {
		if (set.size() == 0) {
			return "None.";
		}
		String[] strings = set.toArray(new String[] {});
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
			if (i == (strings.length - 1)) {
				sb.append(".");
			} else {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	private DescriptionPane() {
		super();
		this.setEditable(false);
		this.setContentType("text/html; charset=UTF-8");

		// read template
		try {
			FileReader fr = new FileReader(new File("log-template.html"));
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String read = "";
			while ((read = br.readLine()) != null) {
				sb.append(read.trim());
			}
			br.close();
			fr.close();
			DescriptionPane.template = sb.toString();
		} catch (Exception e) {
			DescriptionPane.template = "<html>Template file read problem !" + e
			    + "</html>";
		}
		this.updateFrame(1);
	}

	public void updateFrame(final Integer virtualClockId) {
		String text = DescriptionPane.template;
		// now process the events
		Set<String> arrivals = new TreeSet<String>();
		Set<String> completions = new TreeSet<String>();
		Set<String> cancellations = new TreeSet<String>();
		Set<String> executions = new TreeSet<String>();
		Set<String> restarts = new TreeSet<String>();
		Set<String> failures = new TreeSet<String>();
		Set<String> movesGood = new TreeSet<String>();
		Set<String> movesBad = new TreeSet<String>();
		List<Event> evts = Collections.synchronizedList(Event
		    .getEventsInTick(virtualClockId));
		for (Event evt : evts) {
			Integer type = evt.getType().getId();
			if (type.equals(EventType.EVENT_JOB_ARRIVAL)) {
				// job arrived
				arrivals.add("#" + evt.getJob());
			} else if (type.equals(EventType.EVENT_JOB_COMPLETION)) {
				// job completed
				completions.add("#" + evt.getJob());
			} else if (type.equals(EventType.EVENT_JOB_CANCEL)) {
				// job cancelled
				cancellations.add("#" + evt.getJob());
			} else if (type.equals(EventType.EVENT_JOB_EXECUTION_START)) {
				// job started executing
				executions.add("#" + evt.getJob());
			} else if (type.equals(EventType.EVENT_MACHINE_RESTART)
			    || type.equals(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_GOOD)
			    || type.equals(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_BAD)) {
				// machine restart
				restarts.add(evt.getSourceMachine().getName());
			} else if (type.equals(EventType.EVENT_MACHINE_FAILURE)
			    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)
			    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
				// machine failure
				failures.add(evt.getSourceMachine().getName());
			}
			if (type.equals(EventType.EVENT_JOB_MOVE_GOOD)
			    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)
			    || type.equals(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_GOOD)) {
				// good move
				movesGood.add("#" + evt.getJob() + "("
				    + evt.getSourceMachine().getName() + ">"
				    + evt.getTargetMachine().getName() + ")");
			} else if (type.equals(EventType.EVENT_JOB_MOVE_BAD)
			    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)
			    || type.equals(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_BAD)) {
				// bad move
				movesBad.add("#" + evt.getJob() + "("
				    + evt.getSourceMachine().getName() + ">"
				    + evt.getTargetMachine().getName() + ")");
			}
		}
		// fill some basic information
		text = text.replaceAll("\\Q${CLOCK}\\E", Event.getLastWithVirtualClock(
		    virtualClockId).toString());
		text = text.replaceAll("\\Q${VIRTUALCLOCK}\\E", virtualClockId.toString());
		text = text.replaceAll("\\Q${RESTARTS}\\E", DescriptionPane
		    .setToString(restarts));
		text = text.replaceAll("\\Q${FAILURES}\\E", DescriptionPane
		    .setToString(failures));
		text = text.replaceAll("\\Q${ARRIVALS}\\E", DescriptionPane
		    .setToString(arrivals));
		text = text.replaceAll("\\Q${STARTS}\\E", DescriptionPane
		    .setToString(executions));
		text = text.replaceAll("\\Q${COMPLETIONS}\\E", DescriptionPane
		    .setToString(completions));
		text = text.replaceAll("\\Q${CANCELLATIONS}\\E", DescriptionPane
		    .setToString(cancellations));
		text = text.replaceAll("\\Q${GOOD_MOVES}\\E", DescriptionPane
		    .setToString(movesGood));
		text = text.replaceAll("\\Q${BAD_MOVES}\\E", DescriptionPane
		    .setToString(movesBad));
		// now fill the information we just computed
		this.setText(text);
	}
}