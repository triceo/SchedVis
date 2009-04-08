package cz.muni.fi.spc.SchedVis;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.rendering.ScheduleRenderer;

public class Benchmark {

	public static void run() {
		Set<Machine> machines = Machine.getAllGroupless();
		new ScheduleRenderer(machines.toArray(new Machine[] {})[0], 1);
		Double totalTime = 0.0;
		for (int i = 0; i < 10; i++) {
			ExecutorService e = Executors.newFixedThreadPool(1);
			Long now = System.nanoTime();
			for (Integer clock : new Integer[] { 1, 500, 1000, 1500, 2000 }) {
				for (Machine m : machines) {
					ScheduleRenderer mr = new ScheduleRenderer(m, clock);
					e.submit(mr);
					try {
						mr.get();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			Double time = (System.nanoTime() - (double) now) / 1000 / 1000 / 1000;
			System.out.println(i + " run: " + time);
			totalTime += time;
		}
		System.out.println("Per one: " + (totalTime / 10.0));
		return;
	}
}
