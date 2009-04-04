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

import cz.muni.fi.spc.SchedVis.Configuration;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author triceo
 * 
 */
public class ScheduleRenderingController {

	private final static ScheduleRenderingController c = new ScheduleRenderingController();

	public static ScheduleRenderingController getInstance() {
		return ScheduleRenderingController.c;
	}

	private final Map<Integer, Map<Machine, MachineRenderer>> renderers = new HashMap<Integer, Map<Machine, MachineRenderer>>();
	private final Map<Integer, Map<Machine, Image>> images = new HashMap<Integer, Map<Machine, Image>>();

	private final ExecutorService e = Executors.newCachedThreadPool();

	private final ExecutorService fe = Executors.newFixedThreadPool(Configuration
	    .getNumberOfCPUCores() * 2);

	public synchronized Image getRendered(final Machine m, final Integer clock) {
		if (this.images.containsKey(clock) && this.images.get(clock).containsKey(m)) {
			return this.images.get(clock).get(m);
		}
		if (!this.renderers.containsKey(clock)
		    || !this.renderers.get(clock).containsKey(m)) {
			this.render(m, clock);
		}
		MachineRenderer mr = this.renderers.get(clock).get(m);
		Image img;
		try {
			img = mr.get();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
		this.renderers.get(clock).remove(m);
		if (!this.images.containsKey(clock)) {
			this.images.put(clock, new HashMap<Machine, Image>());
		}
		this.images.get(clock).put(m, img);
		return img;
	}

	public synchronized void render(final Machine m, final Integer clock) {
		if (!this.renderers.containsKey(clock)) {
			this.renderers.put(clock, new HashMap<Machine, MachineRenderer>());
		}
		Map<Machine, MachineRenderer> map = this.renderers.get(clock);
		if (!map.containsKey(m)) {
			MachineRenderer mr = new MachineRenderer(m, clock, this.fe);
			this.e.submit(mr);
			map.put(m, mr);
		}
	}

}
