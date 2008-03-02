/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.models;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

import cz.muni.fi.spc.SchedVis.model.entities.Timeline;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class TimelineSliderModel extends DefaultBoundedRangeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8706999823450177356L;

	/**
	 * 
	 */
	public TimelineSliderModel(final ChangeListener listener) {
		this.setMinimum(Timeline.getMinClock().intValue());
		this.setMaximum(Timeline.getMaxClock().intValue());
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
