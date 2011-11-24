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

import java.util.Formatter;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	CountDownLatch l = new CountDownLatch(1);
	private static boolean doesPlay = false;

	private static final Logger logger = LoggerFactory.getLogger(Player.class);

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
	public void run() {
		while (true) {
			try {
				this.l.await();
				this.l = new CountDownLatch(1);
			} catch (final Exception e) {
				Player.logger
				    .warn(new Formatter().format(Messages.getString("Player.0"),
				        e.getLocalizedMessage()).toString());
			}
			Player.logger.debug(Messages.getString("Player.1"));
			while (Player.doesPlay) {
				// invoke the code in the event-processing thread...
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final TimelineSliderModel m = TimelineSliderModel.getInstance();
						m.setValue(Event.getNext(m.getRichValue()));
					}
				});
				// .. and wait
				try {
					Thread.sleep(Configuration.getPlayDelay());
				} catch (final Exception e) {
					Player.logger.warn(new Formatter().format(
					    Messages.getString("Player.2"), e.getLocalizedMessage())
					    .toString());
				}
			}
			Player.logger.debug(Messages.getString("Player.3"));
		}
	}

	/**
	 * Wake the thread from its sleep.
	 */
	public void toggleStatus() {
		Player.logger.debug(Messages.getString("Player.4"));
		Player.doesPlay = !Player.doesPlay;
		this.l.countDown();
		Player.logger.debug(Messages.getString("Player.5"));
	}
}
