/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;

/**
 * Implements a timeline "widget," used to move forward or backwards on the
 * timeline.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Timeline implements UIElement, ChangeListener {

	private JPanel sliderPane = null;
	private TimelineSliderModel tlsm = null;
	private final JLabel sliderLabel;

	/**
	 * 
	 */
	public Timeline() {
		this.sliderPane = new JPanel();
		this.sliderPane.setLayout(new BorderLayout());
		// left-side buttons
		final JPanel innerPane = new JPanel();
		innerPane.setLayout(new FlowLayout());
		innerPane.add(new JButton("|<"));
		innerPane.add(new JButton("<<"));
		innerPane.add(new JButton("<"));
		// middle slider
		this.sliderPane.add(innerPane, BorderLayout.LINE_START);
		final JTimelineSlider slider = new JTimelineSlider();
		this.tlsm = new TimelineSliderModel(this);
		slider.setModel(this.tlsm);
		this.sliderPane.add(slider, BorderLayout.CENTER);
		// right-side buttons
		final JPanel innerPane2 = new JPanel();
		this.sliderLabel = new JLabel(this.getSliderDescription(slider
				.getValue(), slider.getMaximum()));
		innerPane2.add(this.sliderLabel, BorderLayout.LINE_END);
		innerPane2.setLayout(new FlowLayout());
		innerPane2.add(new JButton(">"));
		innerPane2.add(new JButton(">>"));
		innerPane2.add(new JButton(">|"));
		this.sliderPane.add(innerPane2, BorderLayout.LINE_END);
	}

	@Override
	public JPanel get() {
		return this.sliderPane;
	}

	private String getSliderDescription(final Integer value,
			final Integer maximum) {
		return value + "/" + maximum;
	}

	public void stateChanged(final ChangeEvent e) {
		if (e.getSource().equals(this.tlsm)) {
			final TimelineSliderModel tlsm = (TimelineSliderModel) e
					.getSource();
			this.sliderLabel.setText(this.getSliderDescription(tlsm.getValue(),
					tlsm.getMaximum()));
		}
	}

}
