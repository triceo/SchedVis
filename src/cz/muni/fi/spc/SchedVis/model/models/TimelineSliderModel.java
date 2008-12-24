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
	this.setMinimum(Event.getFirst().getId());
	this.setMaximum(Event.getLast().getId());
	this.setValue(this.getMinimum());
	this.addChangeListener(listener);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public TimelineSliderModel(final int arg0, final int arg1, final int arg2,
	    final int arg3) {
	super(arg0, arg1, arg2, arg3);
	// TODO Auto-generated constructor stub
    }

}
