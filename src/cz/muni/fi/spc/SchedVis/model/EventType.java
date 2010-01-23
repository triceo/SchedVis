/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.muni.fi.spc.SchedVis.model;

public enum EventType {
	JOB_ARRIVAL("job-arrival", 1), JOB_EXECUTION_START("job-execution-start", 2), JOB_CANCEL(
	    "job-cancel", 3), MOVE_GOOD("good-move", 4), MOVE_BAD("bad-move", 5), MACHINE_FAIL(
	    "machine-failure", 6), MACHINE_FAIL_MOVE_GOOD(
	    "machine-failure-move-good", 7), MACHINE_FAIL_MOVE_BAD(
	    "machine-failure-move-good", 8), MACHINE_RESTART("machine-restart", 9), JOB_COMPLETION(
	    "job-completion", 10);

	public static EventType getWithId(final int id) {
		for (final EventType et : EventType.values()) {
			if (et.getId() == id) {
				return et;
			}
		}
		return null;
	}

	public static EventType getWithName(final String name) {
		for (final EventType et : EventType.values()) {
			if (et.getName().equals(name)) {
				return et;
			}
		}
		return null;
	}

	private String name;

	private int id;

	EventType(final String name, final int id) {
		this.setName(name);
		this.setId(id);
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
