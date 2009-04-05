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
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * This class serves as a container for all the schedule rendering. If there is
 * no rendered schedule stored in memory, it does everything possible to make it
 * so.
 * 
 * It is a singleton as we only need one instance of this class.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class ScheduleRenderingController {

	private final static ScheduleRenderingController c = new ScheduleRenderingController();

	public static ScheduleRenderingController getInstance() {
		return ScheduleRenderingController.c;
	}

	/**
	 * Holds active machine renderers, ie. schedules that are still rendering.
	 */
	private final Map<Integer, Map<Machine, ScheduleRenderer>> renderers = new HashMap<Integer, Map<Machine, ScheduleRenderer>>();

	/**
	 * Executor for rendering schedules.
	 */
	private final ExecutorService e = Executors.newCachedThreadPool();

	/**
	 * Requests an already rendered schedule. If none is available but the
	 * rendering is already in progress, it waits until it finishes and then
	 * returns the result. Otherwise it starts the rendering and returns its
	 * result when done.
	 * 
	 * @param m
	 *          Machine in question.
	 * @param clock
	 *          The time in which to render the schedule.
	 * @return The rendered schedule.
	 */
	public synchronized Image getRendered(final Machine m, final Integer clock) {
		if (!this.renderers.containsKey(clock)
		    || !this.renderers.get(clock).containsKey(m)) {
			this.render(m, clock);
			return this.getRendered(m, clock); // start all over again
		}
		ScheduleRenderer mr = this.renderers.get(clock).get(m);
		Image img;
		try {
			img = mr.get();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
		this.renderers.get(clock).remove(m);
		return img;
	}

	/**
	 * Start rendering of a schedule. Has no effect when the rendering already
	 * started or finished.
	 * 
	 * @param m
	 *          Machine in question.
	 * @param clock
	 *          The time in which to render the schedule.
	 */
	public synchronized void render(final Machine m, final Integer clock) {
		if (!this.renderers.containsKey(clock)) {
			this.renderers.put(clock, new HashMap<Machine, ScheduleRenderer>());
		}
		Map<Machine, ScheduleRenderer> map = this.renderers.get(clock);
		if (this.renderers.get(clock).containsKey(m)) {
			return;
		}
		ScheduleRenderer mr = new ScheduleRenderer(m, clock, this.e);
		this.e.submit(mr);
		map.put(m, mr);
	}

}
