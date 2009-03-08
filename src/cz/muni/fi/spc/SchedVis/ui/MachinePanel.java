/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * @author triceo
 *
 */
public class MachinePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1407665978399872917L;
    protected Image ci = null;

    public Image getDisplayedImage() {
	return this.ci;
    }

    @Override
    public void paint(final Graphics g) {
	this.update(g);
    }

    public void setImage(final Image si) {
	this.ci = si;
	this.setPreferredSize(new Dimension(this.ci.getWidth(null) + 1, this.ci
		.getHeight(null) + 1));
	this.validate();
	this.repaint();

    }

    @Override
    public void update(final Graphics g) {
	if (this.ci!=null) {
	    g.drawImage(this.ci, 0, 0, null);
	} else {
	    super.update(g);
	}
    }
}
