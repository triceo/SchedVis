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
package cz.muni.fi.spc.SchedVis.ui;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class TimelineSlider extends JSlider {

	/**
     * 
     */
	private static final long serialVersionUID = -8327074009782836875L;

	/**
     * 
     */
	public TimelineSlider() {
		this.specialize();
	}

	/**
	 * @param arg0
	 */
	public TimelineSlider(final BoundedRangeModel arg0) {
		super(arg0);
		this.specialize();
	}

	/**
	 * @param arg0
	 */
	public TimelineSlider(final int arg0) {
		super(arg0);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TimelineSlider(final int arg0, final int arg1) {
		super(arg0, arg1);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public TimelineSlider(final int arg0, final int arg1, final int arg2) {
		super(arg0, arg1, arg2);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public TimelineSlider(final int arg0, final int arg1, final int arg2,
	    final int arg3) {
		super(arg0, arg1, arg2, arg3);
		this.specialize();
	}

	private int calcMajorTickSpacing() {
		final Integer valueCount = this.getMaximum() - this.getMinimum();
		return valueCount / 10;
	}

	private int calcMinorTickSpacing() {
		final Integer valueCount = this.getMaximum() - this.getMinimum();
		return valueCount / 50;
	}

	@Override
	public void setModel(final BoundedRangeModel model) {
		super.setModel(model);
		this.setMajorTickSpacing(this.calcMajorTickSpacing());
		this.setMinorTickSpacing(this.calcMinorTickSpacing());
	}

	private void specialize() {
		this.setPaintTicks(true);
	}

}
