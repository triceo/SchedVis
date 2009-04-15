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
package cz.muni.fi.spc.SchedVis.background;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.models.TimelineSliderModel;
import cz.muni.fi.spc.SchedVis.util.Configuration;
import cz.muni.fi.spc.SchedVis.util.ScheduleRenderingController;

public final class LookAhead implements Runnable {

	private static final Integer LOOKAHEAD_FAN = Configuration
	    .getNumberOfSchedulesLookahead();
	private static final Integer LOOKABACK_FAN = Configuration
	    .getNumberOfSchedulesLookaback();
	private static final TimelineSliderModel model = TimelineSliderModel
	    .getInstance();

	private static final Set<Machine> ms = Machine.getAllGroupless();
	private static final ExecutorService s = Executors.newFixedThreadPool(1);

	public static void submit() {
		LookAhead.s.submit(new LookAhead());
	}

	@Override
	public void run() {
		if (LookAhead.model.getValueIsAdjusting()) {
			// do not look ahead when the value is uncertain
			return;
		}
		Integer currentClock = LookAhead.model.getValue();
		ScheduleRenderingController.cleanup(currentClock, LookAhead.LOOKABACK_FAN,
		    LookAhead.LOOKAHEAD_FAN);
		// look ahead of time
		Set<Integer> clocks = new HashSet<Integer>();
		Integer prevClock = currentClock;
		for (int i = 0; i <= LookAhead.LOOKAHEAD_FAN; i++) {
			prevClock = Event.getNext(prevClock).getVirtualClock();
			clocks.add(prevClock);
		}
		// look back in time
		prevClock = currentClock;
		for (int i = 0; i < LookAhead.LOOKABACK_FAN; i++) {
			prevClock = Event.getPrevious(prevClock).getVirtualClock();
			clocks.add(prevClock);
		}
		// now submit all
		for (Integer clock : clocks) {
			for (Machine m : LookAhead.ms) {
				ScheduleRenderingController.render(m, clock);
			}
		}
	}

}
