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
package cz.muni.fi.spc.SchedVis.util;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * The class that implements the "play" functionality. It is a thread that
 * "presses" the "Next" button on the timeline and then falls asleep and waits
 * to be awakened later.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Player implements Runnable {

	private static final Player p = new Player();
	private static boolean doesPlay = false;

	/**
	 * This is a singleton.
	 * 
	 * @return The only available instance.
	 */
	public static Player getInstance() {
		return Player.p;
	}

	/**
	 * Continue playing.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				synchronized (Player.p) {
					Player.p.wait();
				}
			} catch (final Exception e) {
				Logger.getLogger(Player.class).warn(
				    new PrintfFormat(Messages.getString("Player.0")) //$NON-NLS-1$
				        .sprintf(e.getLocalizedMessage()));
			}
			Logger.getLogger(Player.class).debug(Messages.getString("Player.1")); //$NON-NLS-1$
			while (Player.doesPlay) {
				// invoke the code in the event-processing thread...
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final TimelineSliderModel m = TimelineSliderModel.getInstance();
						m.setValue(Event.getNext(m.getRichValue()));
					}
				});
				// .. and wait
				try {
					Thread.sleep(Configuration.getPlayDelay());
				} catch (final Exception e) {
					Logger.getLogger(Player.class).warn(
					    new PrintfFormat(Messages.getString("Player.2")) //$NON-NLS-1$
					        .sprintf(e.getLocalizedMessage()));
				}
			}
			Logger.getLogger(Player.class).debug(Messages.getString("Player.3")); //$NON-NLS-1$
		}
	}

	/**
	 * Wake the thread from its sleep.
	 */
	public void toggleStatus() {
		Logger.getLogger(Player.class).debug(Messages.getString("Player.4")); //$NON-NLS-1$
		Player.doesPlay = !Player.doesPlay;
		synchronized (Player.p) {
			Player.p.notifyAll();
		}
		Logger.getLogger(Player.class).debug(Messages.getString("Player.5")); //$NON-NLS-1$
	}
}
