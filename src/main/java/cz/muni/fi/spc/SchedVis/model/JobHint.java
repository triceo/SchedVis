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

public enum JobHint {
	NONE(0), MOVE_OK(1), MOVE_NOK(2), ARRIVAL(3);

	public static JobHint getWithId(final int id) {
		for (final JobHint et : JobHint.values()) {
			if (et.getId() == id) {
				return et;
			}
		}
		return null;
	}

	private int id;

	JobHint(final int id) {
		this.setId(id);
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}