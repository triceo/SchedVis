/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.util;

import java.awt.Image;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.background.ScheduleRenderer;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * This class serves as a container for all the schedule rendering. If there is
 * no rendered schedule stored in memory, it does everything possible to make it
 * so.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class ScheduleRenderingController {

	/**
	 * Holds active machine renderers, i. e. the schedules that are still
	 * rendering.
	 */
	private final static Map<Integer, Map<Machine, ScheduleRenderer>> renderers = Collections
	    .synchronizedMap(new HashMap<Integer, Map<Machine, ScheduleRenderer>>());

	/**
	 * Executor for rendering schedules.
	 */
	private static ExecutorService e = ScheduleRenderingController
	    .getNewExecutor();

	private static Logger logger = Logger
	    .getLogger(ScheduleRenderingController.class);

	private static ExecutorService getNewExecutor() {
		return Executors.newFixedThreadPool(1);
	}

	/**
	 * Requests an already rendered schedule. If none is available but the
	 * rendering is already in progress, it waits until it is finished and then
	 * returns the result. Otherwise it starts the rendering and returns its
	 * result when done.
	 * 
	 * @param m
	 *          Machine in question.
	 * @param evt
	 *          The event in which to render the schedule.
	 * @return The rendered schedule.
	 */
	public static Image getRendered(final Machine m, final Event evt) {
		synchronized (ScheduleRenderingController.renderers) {
			if (ScheduleRenderingController.renderers.containsKey(evt.getId())
			    && ScheduleRenderingController.renderers.get(evt.getId())
			        .containsKey(m)) {
				// we have the renderer cached
				try {
					final Image img = ScheduleRenderingController.renderers.get(
					    evt.getId()).get(m).get();
					ScheduleRenderingController.renderers.get(evt.getId()).remove(m);
					return img;
				} catch (final Exception e) {
					ScheduleRenderingController.logger.error(new PrintfFormat(Messages
					    .getString("ScheduleRenderingController.0")) //$NON-NLS-1$
					    .sprintf(new Object[] { m.getName(), evt.getId(),
					        e.getLocalizedMessage() }));
					return null;
				}
			}
		}
		// get the renderer
		ScheduleRenderingController.render(m, evt);
		return ScheduleRenderingController.getRendered(m, evt);
	}

	/**
	 * Start rendering of a schedule. Has no effect when the rendering already
	 * started or finished.
	 * 
	 * @param m
	 *          Machine in question.
	 * @param evt
	 *          The event in which to render the schedule.
	 */
	public static void render(final Machine m, final Event evt) {
		synchronized (ScheduleRenderingController.renderers) {
			if (ScheduleRenderingController.renderers.containsKey(evt.getId())
			    && ScheduleRenderingController.renderers.get(evt.getId())
			        .containsKey(m)) {
				// don't render when we already have the result
				return;
			}
			if (!ScheduleRenderingController.renderers.containsKey(evt.getId())) {
				ScheduleRenderingController.renderers.put(evt.getId(), Collections
				    .synchronizedMap(new HashMap<Machine, ScheduleRenderer>()));
			}
		}
		ScheduleRenderingController.renderers.get(evt.getId()).put(m,
		    new ScheduleRenderer(m, evt));
		ScheduleRenderingController.e.submit(ScheduleRenderingController.renderers
		    .get(evt.getId()).get(m));
	}

	public static void restart() {
		ScheduleRenderingController.e.shutdown();
		while (!ScheduleRenderingController.e.isTerminated()) {
			try {
				Thread.sleep(1000);
			} catch (final Exception ex) {
				// ignore
			}
		}
		ScheduleRenderingController.e = ScheduleRenderingController
		    .getNewExecutor();
	}
}
