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
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;

/**
 * A model for the slider that specifies which time will the schedules be
 * rendered at.
 * 
 * It is a singleton as the slider is also only one.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class TimelineSliderModel extends DefaultBoundedRangeModel {

	private static final long serialVersionUID = -8706999823450177356L;

	private static TimelineSliderModel model = null;

	/**
	 * Get the only instance of the class.
	 * 
	 * @return The instance
	 * @throws IllegalArgumentException
	 *           Thrown when this method was called before
	 *           setting the listener first.
	 */
	public static TimelineSliderModel getInstance()
	    throws IllegalArgumentException {
		return TimelineSliderModel.getInstance(null);
	}

	/**
	 * Get the only instance of the class. The listener is only set if it wasn't
	 * set previously.
	 * 
	 * @param listener
	 *          What should listen on changes to this model.
	 * @return The instance.
	 */
	public static TimelineSliderModel getInstance(final ChangeListener listener) {
		if (TimelineSliderModel.model == null) {
			TimelineSliderModel.model = new TimelineSliderModel(listener);
		} else if (listener != null) {
			if (TimelineSliderModel.model.getChangeListeners().length == 0) {
				TimelineSliderModel.model.addChangeListener(listener);
			} else {
				Logger.getLogger(TimelineSliderModel.class).warn(
				    "Listener has already been set. This will have no effect.");
			}
		}
		return TimelineSliderModel.model;
	}

	/**
	 * Class constructor.
	 * 
	 * @param listener
	 *          What should listen on changes to the model.
	 */
	private TimelineSliderModel(final ChangeListener listener) {
		this.setMinimum(Event.getFirst().getId());
		this.setMaximum(Event.getLast().getId());
		this.setValue(this.getMinimum());
		this.addChangeListener(listener);
	}

}
