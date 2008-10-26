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

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;

/**
 * Implements a timeline "widget," used to move forward or backwards on the
 * timeline.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class SliderPanel extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6091479520934383104L;
	private TimelineSliderModel tlsm = null;
	private final JLabel sliderLabel;

	/**
	 * 
	 */
	public SliderPanel() {
		this.setLayout(new BorderLayout());
		// left-side buttons
		final JPanel innerPane = new JPanel();
		innerPane.setLayout(new FlowLayout());
		innerPane.add(new JButton("|<"));
		innerPane.add(new JButton("<<"));
		innerPane.add(new JButton("<"));
		// middle slider
		this.add(innerPane, BorderLayout.LINE_START);
		final TimelineSlider slider = new TimelineSlider();
		this.tlsm = new TimelineSliderModel(this);
		slider.setModel(this.tlsm);
		this.add(slider, BorderLayout.CENTER);
		// right-side buttons
		final JPanel innerPane2 = new JPanel();
		this.sliderLabel = new JLabel(this.getSliderDescription(slider
				.getValue(), slider.getMaximum()));
		innerPane2.add(this.sliderLabel, BorderLayout.LINE_END);
		innerPane2.setLayout(new FlowLayout());
		innerPane2.add(new JButton(">"));
		innerPane2.add(new JButton(">>"));
		innerPane2.add(new JButton(">|"));
		this.add(innerPane2, BorderLayout.LINE_END);
	}

	private String getSliderDescription(final Integer value,
			final Integer maximum) {
		return value + "/" + maximum;
	}

	public void stateChanged(final ChangeEvent e) {
		if (e.getSource().equals(this.tlsm)) {
			final TimelineSliderModel tlsm = (TimelineSliderModel) e
					.getSource();
			tlsm.setValue(Event.getPrevious(tlsm.getValue()).getId());
			this.sliderLabel.setText(this.getSliderDescription(tlsm.getValue(),
					tlsm.getMaximum()));
		}
	}

}
