/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import cz.muni.fi.spc.SchedVis.model.SQL;



/**
 * Implements a timeline "widget," used to move forward or backwards on the
 * timeline. 
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Timeline implements UIElement, ModelAccepting {

	private JPanel sliderPane = null;

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
		final JSlider slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setExtent(0);
		slider.setMinimum(1);
		slider.setMaximum(100);
		slider.setMinorTickSpacing(slider.getMaximum() / 20);
		slider.setMajorTickSpacing(slider.getMaximum() / 5);
		slider.setValue(slider.getMinimum());
		slider.setMinimumSize(new Dimension(this.getSliderMinimumSize(slider
				.getMinimum(), slider.getMaximum(), slider.getExtent()), 0));
		this.sliderPane.add(slider, BorderLayout.CENTER);
		// right-side buttons
		final JPanel innerPane2 = new JPanel();
		innerPane2.add(new JPanel().add(new JTextArea(slider.getValue() + "/"
				+ slider.getMaximum())), BorderLayout.LINE_END);
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

	private Integer getSliderMinimumSize(final Integer min, final Integer max,
			final Integer extent) {
		final Integer value = (max / (extent + 1)) * 3;
		if (value >= max) {
			return value;
		} else {
			return 100;
		}
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setModel(final SQL model) {
	}

}
