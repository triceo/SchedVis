/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * Implements a panel with a file-picking widget.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JFilePicker extends JLabeledField implements ActionListener {

	private static String ACTION_BUTTON_CLICKED = "Button clicked";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2937947660051686598L;
	private JFileFilter filter;
	private JButton btn;

	/**
	 * @param arg0
	 */
	public JFilePicker(final boolean arg0, final String label,
			final JFileFilter filter) {
		super(label, arg0);
		this.specialize(filter);
	}

	/**
	 * 
	 */
	public JFilePicker(final String label, final JFileFilter filter) {
		super(label);
		this.specialize(filter);
	}

	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals(JFilePicker.ACTION_BUTTON_CLICKED)) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(this.filter);
			final int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.setValue(chooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.btn.setEnabled(enabled);
	}

	private void specialize(final JFileFilter filter) {
		this.filter = filter;
		this.btn = new JButton("Open a File...");
		this.btn.addActionListener(this);
		this.btn.setActionCommand(JFilePicker.ACTION_BUTTON_CLICKED);
		this.add(this.btn);
	}

}
