/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * A panel that shows a machine schedule.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class MachinePanel extends JPanel {

	private static final long serialVersionUID = 1407665978399872917L;
	private Image i = null;

	/**
	 * Get the schedule image this panel holds.
	 * 
	 * @return The schedule.
	 */
	public Image getImage() {
		return this.i;
	}

	/**
	 * Refresh the panel on the screen.
	 */
	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		if (this.getImage() != null) {
			g.drawImage(this.getImage(), 0, 0, null);
		}
	}

	/**
	 * Set the schedule image to be rendered in this panel.
	 * 
	 * @param i
	 *          The schedule.
	 */
	public void setImage(final Image i) {
		this.i = i;
		this.setPreferredSize(new Dimension(this.getImage().getWidth(null) + 5,
		    this.getImage().getHeight(null) + 5));
	}

}
