/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import cz.muni.fi.spc.SchedVis.model.Importer;
import cz.muni.fi.spc.SchedVis.model.SQL;

/**
 * Creates the dialog to choose where to get schedule data from.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class ImportDialog extends JDialog implements ActionListener {

	private static String ACTION_NEW_BUTTON_CLICKED = "NewButton clicked.";
	private static String ACTION_OLD_BUTTON_CLICKED = "OldButton clicked.";
	private static String ACTION_SUBMIT_BUTTON_CLICKED = "SubmitButton clicked.";
	/**
	 * 
	 */
	private static final long serialVersionUID = -23334905986074228L;
	private final JFilePicker[] filePickers = new JFilePicker[3];
	private JTextField fileName;
	private JLabel fileNameLabel;
	private JButton submitButton;
	private ButtonGroup bg;

	/**
	 * 
	 */
	public ImportDialog() {
		super();
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public ImportDialog(final Dialog owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public ImportDialog(final Dialog owner, final boolean modal) {
		super(owner, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public ImportDialog(final Dialog owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public ImportDialog(final Dialog owner, final String title,
			final boolean modal) {
		super(owner, title, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public ImportDialog(final Dialog owner, final String title,
			final boolean modal, final GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public ImportDialog(final Frame owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public ImportDialog(final Frame owner, final boolean modal) {
		super(owner, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public ImportDialog(final Frame owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public ImportDialog(final Frame owner, final String title,
			final boolean modal) {
		super(owner, title, modal);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public ImportDialog(final Frame owner, final String title,
			final boolean modal, final GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.specialize();
	}

	/**
	 * @param owner
	 */
	public ImportDialog(final Window owner) {
		super(owner);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param modalityType
	 */
	public ImportDialog(final Window owner, final ModalityType modalityType) {
		super(owner, modalityType);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public ImportDialog(final Window owner, final String title) {
		super(owner, title);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public ImportDialog(final Window owner, final String title,
			final ModalityType modalityType) {
		super(owner, title, modalityType);
		this.specialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 * @param gc
	 */
	public ImportDialog(final Window owner, final String title,
			final ModalityType modalityType, final GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		this.specialize();
	}

	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		// handle picker widgets
		if (command.equals(ImportDialog.ACTION_NEW_BUTTON_CLICKED)) {
			this.filePickers[0].setEnabled(true);
			this.filePickers[1].setEnabled(true);
			this.fileName.setEnabled(true);
			this.fileNameLabel.setEnabled(true);
			this.filePickers[2].setEnabled(false);
		} else if (command.equals(ImportDialog.ACTION_OLD_BUTTON_CLICKED)) {
			this.filePickers[0].setEnabled(false);
			this.filePickers[1].setEnabled(false);
			this.fileName.setEnabled(false);
			this.fileNameLabel.setEnabled(false);
			this.filePickers[2].setEnabled(true);
		} else if (command.equals(ImportDialog.ACTION_SUBMIT_BUTTON_CLICKED)) {
			final Enumeration<AbstractButton> elems = this.bg.getElements();
			if (this.bg.isSelected(elems.nextElement().getModel())) {
				// first radio selected
				final String filename1 = this.filePickers[0].getFilename();
				final String filename2 = this.filePickers[1].getFilename();
				final String name = this.fileName.getText();
				final File file1 = new File(filename1);
				final File file2 = new File(filename2);
				if (file1.exists() && file2.exists() && (name.length() > 0)) {
					if (this.processSource(file1, file2, name)) {
						this.display(false);
					} else {
						JOptionPane.showMessageDialog(this,
							"Error while processing source files.");
					}
				} else {
					JOptionPane
							.showMessageDialog(this,
									"Please pick valid source files and a correct name.");
				}
			} else if (this.bg.isSelected(elems.nextElement().getModel())) {
				// second radio selected
				final String filename = this.filePickers[2].getFilename();
				final File file = new File(filename);
				if (file.exists()) {
					if (this.processSource(file)) {
						this.display(false);
					} else {
						JOptionPane.showMessageDialog(this,
								"Error while opening data set file.");
					}
				} else {
					JOptionPane.showMessageDialog(this,
							"Please pick a valid data set file.");
				}
			} else {
				// error
				JOptionPane.showMessageDialog(this,
						"Please pick at least one data source.");
			}

		}
	}

	public void display(final boolean visible) {
		this.setVisible(visible);
	}

	/**
	 * Process already existing data set.
	 * 
	 * @param inputFile
	 *            Data set file to process.
	 */
	private boolean processSource(final File inputFile) {
		final String[] parts = inputFile.getName().split("\\.");
		try {
			SQL.getInstance(parts[0], false);
		} catch (final Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Process a new data set.
	 * 
	 * @param machineFile
	 *            File with data about machines.
	 * @param dataFile
	 *            File with data about events.
	 * @param name
	 *            Name for the new dataset.
	 */
	// FIXME Damn this stupid dialog. Refactor somehow.
	private boolean processSource(final File machineFile, final File dataFile,
			final String name) {
		JDialog dialog = new JDialog(this);
		dialog.setModal(false);
		dialog.setAlwaysOnTop(true);
		dialog.setTitle("Processing source files...");
		dialog.setMinimumSize(new Dimension(450, 71));
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JLabel("Processing your source files may take a while."));
		panel.add(new JLabel("You might want to consider having yourself a nice warm cup of Java."));
		panel.add(new JLabel("Or whatever. Enjoy."));
		dialog.add(panel);
		dialog.setVisible(true);		
		boolean result = Importer.process(machineFile, dataFile, name);
		dialog.setVisible(false);
		return result;
	}

	private void specialize() {
		this.setLayout(new BorderLayout());
		final JBorderedPanel pane = new JBorderedPanel("Available data sources");
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		this.add(pane, BorderLayout.CENTER);
		// setup file filters
		final JFileFilter sqlFilter = new JFileFilter();
		sqlFilter.setDescription("SchedVis dataset (*.sqlite)");
		sqlFilter.addType("sqlite");
		// gather filePickers
		final JFileFilter dtaFilter = new JFileFilter();
		dtaFilter.setDescription("SchedVis source files (*.txt)");
		dtaFilter.addType("txt");
		this.filePickers[0] = new JFilePicker("Source for machine data:",
				dtaFilter);
		this.filePickers[1] = new JFilePicker("Source for schedule data:",
				dtaFilter);
		this.filePickers[2] = new JFilePicker("Existing database:", sqlFilter);
		// import new dataset option
		final JPanel rbPane1 = new JPanel();
		rbPane1.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton buttonNew = new JRadioButton();
		buttonNew.setText("Create a new data set:");
		buttonNew.addActionListener(this);
		buttonNew.setActionCommand(ImportDialog.ACTION_NEW_BUTTON_CLICKED);
		rbPane1.add(buttonNew);
		pane.add(rbPane1);
		pane.add(this.filePickers[0]);
		pane.add(this.filePickers[1]);
		final JPanel namePane = new JPanel();
		pane.add(namePane);
		namePane.setLayout(new FlowLayout(FlowLayout.RIGHT)); // start adding
		// name-picking
		// widget
		this.fileNameLabel = new JLabel("Data set name:");
		this.fileName = new JTextField(26);
		this.fileName.setEnabled(false);
		this.fileNameLabel.setLabelFor(this.fileName);
		this.fileNameLabel.setEnabled(false);
		namePane.add(this.fileNameLabel);
		namePane.add(this.fileName);
		// use existing dataset option
		final JPanel rbPane2 = new JPanel();
		rbPane2.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton buttonOld = new JRadioButton();
		buttonOld.addActionListener(this);
		buttonOld.setText("Use existing data set:");
		buttonOld.setActionCommand(ImportDialog.ACTION_OLD_BUTTON_CLICKED);
		rbPane2.add(buttonOld);
		pane.add(rbPane2);
		pane.add(this.filePickers[2]);
		// form button group
		this.bg = new ButtonGroup();
		this.bg.add(buttonNew);
		this.bg.add(buttonOld);
		// submit button
		final JPanel submitPane = new JPanel();
		submitPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.submitButton = new JButton("Continue >>");
		this.submitButton
				.setActionCommand(ImportDialog.ACTION_SUBMIT_BUTTON_CLICKED);
		this.submitButton.addActionListener(this);
		submitPane.add(this.submitButton);
		pane.add(submitPane);
		// some dialog settings
		this.setLocationRelativeTo(this.getParent());
		this.setTitle("Pick data source");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setMinimumSize(new Dimension(560, 295));
		this.setResizable(false);
	}
}
