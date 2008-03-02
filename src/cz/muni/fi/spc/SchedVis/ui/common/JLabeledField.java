/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JLabeledField extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3462092531792287337L;
	private JTextField value;
	private JLabel lbl;

	/**
	 * 
	 */
	public JLabeledField(final String label) {
		this.specialize(label);
	}

	/**
	 * @param isDoubleBuffered
	 */
	public JLabeledField(final String label, final boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.specialize(label);
	}

	/**
	 * @param layout
	 */
	public JLabeledField(final String label, final LayoutManager layout) {
		super(layout);
		this.specialize(label);
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public JLabeledField(final String label, final LayoutManager layout,
			final boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.specialize(label);
	}

	public String getValue() {
		return this.value.getText();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.value.setEnabled(enabled);
		this.lbl.setEnabled(enabled);
	}

	public void setValue(final String text) {
		this.value.setText(text);
	}

	private void specialize(final String label) {
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.value = new JTextField(15);
		this.value.setEnabled(true);
		this.lbl = new JLabel();
		this.lbl.setText(label);
		this.lbl.setLabelFor(this.value);
		this.add(this.lbl);
		this.add(this.value);
	}

}
