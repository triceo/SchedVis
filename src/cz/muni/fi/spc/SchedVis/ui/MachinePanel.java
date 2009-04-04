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
 * @author triceo
 * 
 */
public class MachinePanel extends JPanel {
	/**
     * 
     */
	private static final long serialVersionUID = 1407665978399872917L;
	protected Image ci = null;

	public MachinePanel() {
		super(true);
	}

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
	}

	@Override
	public void update(final Graphics g) {
		if (this.ci != null) {
			g.drawImage(this.ci, 0, 0, null);
		} else {
			super.update(g);
		}
	}
}
