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
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;

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
	private final Machine m;
	private final Event e;

	public MachinePanel(final Machine m, final Event e) {
		this.m = m;
		this.e = e;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Schedule.IMAGE_WIDTH, this.m.getCPUs()
		    * Schedule.NUM_PIXELS_PER_CPU);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Schedule r = new Schedule(this.m, this.e, (Graphics2D) g);
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				r.run();
			} else {
				SwingUtilities.invokeAndWait(r);
			}
		} catch (final InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (final InvocationTargetException ite) {
			ite.printStackTrace();
			throw new IllegalStateException(ite.getMessage());
		}

	}
}
