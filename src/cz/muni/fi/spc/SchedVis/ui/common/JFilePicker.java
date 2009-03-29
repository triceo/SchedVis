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
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import cz.muni.fi.spc.SchedVis.DataFileFilter;

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
	private DataFileFilter filter;
	private JButton btn;

	/**
	 * @param arg0
	 */
	public JFilePicker(final boolean arg0, final String label,
	    final DataFileFilter filter) {
		super(label, arg0);
		this.specialize(filter);
	}

	/**
     * 
     */
	public JFilePicker(final String label, final DataFileFilter filter) {
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

	private void specialize(final DataFileFilter filter) {
		this.filter = filter;
		this.btn = new JButton("Open a File...");
		this.btn.addActionListener(this);
		this.btn.setActionCommand(JFilePicker.ACTION_BUTTON_CLICKED);
		this.add(this.btn);
	}

}
