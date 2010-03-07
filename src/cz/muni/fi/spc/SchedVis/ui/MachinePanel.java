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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * A panel that shows a machine schedule.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class MachinePanel extends JPanel {

	private static final long serialVersionUID = 1407665978399872917L;
	private final Schedule s;
	private final Integer numCPUs;
	private final Dimension d;

	public MachinePanel(final Machine m, final Event e) {
		this.s = new Schedule(m, e);
		this.numCPUs = m.getCPUs();
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.d = new Dimension(Schedule.IMAGE_WIDTH, this.numCPUs
		    * Schedule.NUM_PIXELS_PER_CPU);
	}

	@Override
	public Dimension getPreferredSize() {
		return this.d;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		this.s.setTargetGraphics((Graphics2D) g);
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				this.s.run();
			} else {
				SwingUtilities.invokeAndWait(this.s);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}
}
