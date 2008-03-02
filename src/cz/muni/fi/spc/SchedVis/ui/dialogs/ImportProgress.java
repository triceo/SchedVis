/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import cz.muni.fi.spc.SchedVis.model.Importer;

/**
 * A class defining a dialog to report progress on importing data sources.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ImportProgress extends JDialog implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4859709045315254964L;
	private final Importer task;
	private final JProgressBar pb;

	/**
	 * 
	 */
	public ImportProgress(final JDialog owner, final File file1,
			final File file2, final String name) {
		super(owner, true);
		// start the importing task
		this.task = new Importer(file1, file2, name);
		this.task.addPropertyChangeListener(this);
		this.task.execute();
		// show a dialog
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.setLocationRelativeTo(owner);
		this.setModal(true);
		this.setTitle("Importing selected source files...");
		this.setMinimumSize(new Dimension(350, 60));
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.pb = new JProgressBar();
		this.pb.setMinimum(0);
		this.pb.setMaximum(100);
		this.pb.setStringPainted(true);
		this.pb.setEnabled(true);
		this.add(new JLabel("This may take a while. Please, be patient."),
				BorderLayout.PAGE_END);
		this.add(new JLabel("Progress: "), BorderLayout.LINE_START);
		this.add(this.pb);
		this.setVisible(true);
	}

	public void propertyChange(final PropertyChangeEvent e) {
		if (e.getPropertyName().equals("progress")) {
			this.pb.setValue((Integer) e.getNewValue());
		} else if (e.getPropertyName().equals("state")) {
			if (this.task.isDone()) {
				this.setVisible(false);
				this.setCursor(null);
				if (this.task.isSuccess()) {
					this.getOwner().setVisible(false);
				} else {
					JOptionPane.showMessageDialog(this.getParent(),
							"Error during import.");
				}
			}
		}
	}

}
