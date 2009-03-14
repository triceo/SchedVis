/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
public class SliderPanel extends JPanel implements ChangeListener,
	ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 6091479520934383104L;
    private TimelineSliderModel tlsm = null;
    private final JButton btnStart = new JButton("|<");
    private final JButton btnEnd = new JButton(">|");
    private final JButton btnPrev = new JButton("<");
    private final JButton btnNext = new JButton(">");

    /**
     * 
     */
    public SliderPanel() {
	this.setLayout(new BorderLayout());
	// left-side buttons
	final JPanel innerPane = new JPanel();
	innerPane.setLayout(new FlowLayout());
	this.btnStart.setEnabled(false);
	this.btnPrev.setEnabled(false);
	innerPane.add(this.btnStart);
	innerPane.add(this.btnPrev);
	// middle slider
	this.add(innerPane, BorderLayout.LINE_START);
	final TimelineSlider slider = new TimelineSlider();
	this.tlsm = TimelineSliderModel.getInstance(this);
	slider.setModel(this.tlsm);
	this.add(slider, BorderLayout.CENTER);
	// right-side buttons
	final JPanel innerPane2 = new JPanel();
	innerPane2.setLayout(new FlowLayout());
	innerPane2.add(this.btnNext);
	innerPane2.add(this.btnEnd);
	this.add(innerPane2, BorderLayout.LINE_END);
	// add action listeners to buttons
	final JButton[] buttons = new JButton[] { this.btnStart, this.btnEnd,
		this.btnNext, this.btnPrev };
	for (final JButton b : buttons) {
	    b.addActionListener(this);
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	final Object src = e.getSource();
	if (src.equals(this.btnEnd)) {
	    this.tlsm.setValue(this.tlsm.getMaximum());
	} else if (src.equals(this.btnStart)) {
	    this.tlsm.setValue(this.tlsm.getMinimum());
	} else if (src.equals(this.btnNext) || src.equals(this.btnPrev)) {
	    if (src.equals(this.btnPrev)) {
		this.tlsm.setValue(Event.getPrevious(this.tlsm.getValue())
			.getId());
	    } else {
		this.tlsm.setValue(Event.getNext(this.tlsm.getValue()).getId());
	    }
	}
    }

    public void stateChanged(final ChangeEvent e) {
	final Object src = e.getSource();
	if (src.equals(this.tlsm)) {
	    if (this.tlsm.getValue() <= 1) {
		return;
	    }
	    final Integer value = Event.getPrevious(this.tlsm.getValue())
		    .getId();
	    if (this.tlsm.getValue() != Event.getNext(value).getId()) {
		this.tlsm.setValue(value);
	    }
	    if (value == this.tlsm.getMinimum()) {
		this.btnPrev.setEnabled(false);
		this.btnStart.setEnabled(false);
		this.btnNext.setEnabled(true);
		this.btnEnd.setEnabled(true);
	    } else if (value == this.tlsm.getMaximum()) {
		this.btnPrev.setEnabled(true);
		this.btnStart.setEnabled(true);
		this.btnNext.setEnabled(false);
		this.btnEnd.setEnabled(false);
	    } else {
		this.btnPrev.setEnabled(true);
		this.btnStart.setEnabled(true);
		this.btnNext.setEnabled(true);
		this.btnEnd.setEnabled(true);
	    }
	    if (!this.tlsm.getValueIsAdjusting()) {
		ScheduleTree.getInstance().updateUI();
	    }
	}
    }

}
