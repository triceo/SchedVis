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
public class JTimelineSlider extends JSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8327074009782836875L;

	/**
	 * 
	 */
	public JTimelineSlider() {
		this.specialize();
	}

	/**
	 * @param arg0
	 */
	public JTimelineSlider(final BoundedRangeModel arg0) {
		super(arg0);
		this.specialize();
	}

	/**
	 * @param arg0
	 */
	public JTimelineSlider(final int arg0) {
		super(arg0);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JTimelineSlider(final int arg0, final int arg1) {
		super(arg0, arg1);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JTimelineSlider(final int arg0, final int arg1, final int arg2) {
		super(arg0, arg1, arg2);
		this.specialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JTimelineSlider(final int arg0, final int arg1, final int arg2,
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
