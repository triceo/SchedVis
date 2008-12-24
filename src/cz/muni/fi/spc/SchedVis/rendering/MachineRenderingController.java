/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author triceo
 * 
 */
public class MachineRenderingController {

    private static ExecutorService e = null;

    private static Map<Integer, Map<Machine, Future<JPanel>>> renderers = new HashMap<Integer, Map<Machine, Future<JPanel>>>();

    private static ExecutorService getExecutor() {
	if (MachineRenderingController.e == null) {
	    MachineRenderingController.e = Executors.newCachedThreadPool();
	}
	return MachineRenderingController.e;
    }

    public synchronized static Future<JPanel> getRenderer(final Machine item,
	    final Integer clock) {
	if (!MachineRenderingController.renderers.containsKey(clock)) {
	    MachineRenderingController.renderers.put(clock,
		    new HashMap<Machine, Future<JPanel>>());
	}
	final Map<Machine, Future<JPanel>> rendererMap = MachineRenderingController.renderers
		.get(clock);
	if (!rendererMap.containsKey(item)) {
	    final Callable<JPanel> rm = new MachineRenderer(item, clock,
		    new JPanel());
	    final Future<JPanel> future = MachineRenderingController
		    .getExecutor().submit(rm);
	    rendererMap.put(item, future);
	}
	return rendererMap.get(item);
    }

}
