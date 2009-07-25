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
import java.util.Vector;

import javax.swing.JEditorPane;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.EventType;
import cz.muni.fi.spc.SchedVis.util.Database;

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

	private static String setToString(final Set<String> set, final String color) {
		if (set.size() == 0) {
			return "None.";
		}
		final String[] strings = set.toArray(new String[] {});
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
			if (i == (strings.length - 1)) {
				sb.append(".");
			} else {
				sb.append(",<br/>");
			}
		}
		return "<div style='color: #" + color + "'>" + sb.toString() + "</div>";
	}

	private DescriptionPane() {
		super();
		this.setEditable(false);
		this.setContentType("text/html; charset=UTF-8");

		// read template
		try {
			final FileReader fr = new FileReader(new File("log-template.html"));
			final BufferedReader br = new BufferedReader(fr);
			final StringBuilder sb = new StringBuilder();
			String read = "";
			while ((read = br.readLine()) != null) {
				sb.append(read.trim());
			}
			br.close();
			fr.close();
			DescriptionPane.template = sb.toString();
		} catch (final Exception e) {
			DescriptionPane.template = "<html>Template file read problem !" + e
			    + "</html>";
		}
		this.updateFrame(1);
	}

	public void updateFrame(final Integer eventId) {
		String text = DescriptionPane.template;
		// now process the events
		final Set<String> arrivals = new TreeSet<String>();
		final Set<String> completions = new TreeSet<String>();
		final Set<String> cancellations = new TreeSet<String>();
		final Set<String> executions = new TreeSet<String>();
		final Set<String> restarts = new TreeSet<String>();
		final Set<String> failures = new TreeSet<String>();
		final Set<String> movesGood = new TreeSet<String>();
		final Set<String> movesBad = new TreeSet<String>();
		/*
		 * originally, there were multiple events displayed in the frame. now, there
		 * is only one - but I decided to keep the original code in case there are
		 * more in the future.
		 */
		final List<Event> evts = Collections.synchronizedList(new Vector<Event>());
		final Event event = Database.getEntityManager().find(Event.class, eventId);
		if (event != null) {
			evts.add(event);
		}
		for (final Event evt : evts) {
			Integer type;
			try {
				type = evt.getType().getId();
			} catch (final NullPointerException e) {
				type = null;
			}
			if (type != null) {
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
				} else if (type.equals(EventType.EVENT_MACHINE_RESTART)) {
					// machine restart
					restarts.add(evt.getSourceMachine().getName());
				} else if (type.equals(EventType.EVENT_MACHINE_FAILURE)
				    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)
				    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
					// machine failure
					failures.add(evt.getSourceMachine().getName());
				}
				if (type.equals(EventType.EVENT_JOB_MOVE_GOOD)
				    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)) {
					// good move
					movesGood.add("#" + evt.getJob() + " ("
					    + evt.getSourceMachine().getName() + " > "
					    + evt.getTargetMachine().getName() + ")");
				} else if (type.equals(EventType.EVENT_JOB_MOVE_BAD)
				    || type.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)) {
					// bad move
					movesBad.add("#" + evt.getJob() + " ("
					    + evt.getSourceMachine().getName() + " > "
					    + evt.getTargetMachine().getName() + ")");
				}
			}
		}
		// fill some basic information
		text = text.replaceAll("\\Q${CLOCK}\\E", Event.getClockWithEventId(eventId)
		    .toString());
		text = text.replaceAll("\\Q${EVENTID}\\E", eventId.toString());
		text = text.replaceAll("\\Q${RESTARTS}\\E", DescriptionPane.setToString(
		    restarts, "000000"));
		text = text.replaceAll("\\Q${FAILURES}\\E", DescriptionPane.setToString(
		    failures, "FF0000"));
		text = text.replaceAll("\\Q${ARRIVALS}\\E", DescriptionPane.setToString(
		    arrivals, "00FF00"));
		text = text.replaceAll("\\Q${STARTS}\\E", DescriptionPane.setToString(
		    executions, "FFFF00"));
		text = text.replaceAll("\\Q${COMPLETIONS}\\E", DescriptionPane.setToString(
		    completions, "FF00FF"));
		text = text.replaceAll("\\Q${CANCELLATIONS}\\E", DescriptionPane
		    .setToString(cancellations, "000000"));
		text = text.replaceAll("\\Q${GOOD_MOVES}\\E", DescriptionPane.setToString(
		    movesGood, "00FFFF"));
		text = text.replaceAll("\\Q${BAD_MOVES}\\E", DescriptionPane.setToString(
		    movesBad, "FF0000"));
		// now fill the information we just computed
		this.setText(text);
	}
}
