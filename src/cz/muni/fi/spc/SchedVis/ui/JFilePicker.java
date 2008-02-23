/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Implements a panel with a file-picking widget.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JFilePicker extends JPanel implements ActionListener {

	private static String ACTION_BUTTON_CLICKED = "Button clicked";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2937947660051686598L;
	private JFileFilter filter;
	private JTextField value;
	private JLabel lbl;
	private JButton btn;

	/**
	 * @param arg0
	 */
	public JFilePicker(final boolean arg0, final String label, final JFileFilter filter) {
		super(arg0);
		this.specialize(label, filter);
	}

	/**
	 * 
	 */
	public JFilePicker(final String label, final JFileFilter filter) {
		this.specialize(label, filter);
	}

	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals(JFilePicker.ACTION_BUTTON_CLICKED)) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(this.filter);
			final int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.value.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	public String getFilename() {
		return this.value.getText();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.value.setEnabled(enabled);
		this.lbl.setEnabled(enabled);
		this.btn.setEnabled(enabled);
	}

	private void specialize(final String label, final JFileFilter filter) {
		this.filter = filter;
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.value = new JTextField(15);
		this.value.setEnabled(true);
		this.lbl = new JLabel();
		this.lbl.setText(label);
		this.lbl.setLabelFor(this.value);
		this.add(this.lbl);
		this.add(this.value);
		this.btn = new JButton("Open a File...");
		this.btn.addActionListener(this);
		this.btn.setActionCommand(JFilePicker.ACTION_BUTTON_CLICKED);
		this.add(this.btn);
		this.setEnabled(false);
	}

}
