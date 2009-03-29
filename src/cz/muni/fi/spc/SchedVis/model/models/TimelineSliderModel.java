/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class TimelineSliderModel extends DefaultBoundedRangeModel {

	/**
     * 
     */
	private static final long serialVersionUID = -8706999823450177356L;

	private static TimelineSliderModel model = null;

	public static TimelineSliderModel getInstance()
	    throws IllegalArgumentException {
		if (TimelineSliderModel.model == null) {
			throw new IllegalArgumentException(
			    "You need to set a listener first time you call this method!");
		}
		return TimelineSliderModel.getInstance(null);
	}

	public static TimelineSliderModel getInstance(final ChangeListener listener) {
		if (TimelineSliderModel.model == null) {
			TimelineSliderModel.model = new TimelineSliderModel(listener);
		} else if (listener != null) {
			Logger.getLogger(TimelineSliderModel.class).warn(
			    "Listener has already been set. This will have no effect.");
		}
		return TimelineSliderModel.model;
	}

	/**
     * 
     */
	private TimelineSliderModel(final ChangeListener listener) {
		this.setMinimum(Event.getFirst().getClock());
		this.setMaximum(Event.getLast().getClock());
		this.setValue(this.getMinimum());
		this.addChangeListener(listener);
	}

}
