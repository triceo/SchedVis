/**
 * 
 */
package cz.muni.fi.spc.SchedVis.rendering;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

import cz.muni.fi.spc.SchedVis.model.entities.Machine;

/**
 * @author triceo
 *
 */
public class MachineRenderingController {

    private static Executor e = null;

    private static Map<Integer, Map<Machine, MachineRenderer>> renderers = new ConcurrentHashMap<Integer, Map<Machine, MachineRenderer>>();

    private static Executor getExecutor() {
	if (MachineRenderingController.e == null) {
	    MachineRenderingController.e = Executors.newCachedThreadPool();
	}
	return MachineRenderingController.e;
    }

    public synchronized static MachineRenderer getRenderer(final Machine item,
	    final Integer clock) {
	if (!MachineRenderingController.renderers.containsKey(clock)) {
	    MachineRenderingController.renderers.put(clock,
		    new ConcurrentHashMap<Machine, MachineRenderer>());
	}
	final Map<Machine, MachineRenderer> rendererMap = MachineRenderingController.renderers
	.get(clock);
	if (!rendererMap.containsKey(item)) {
	    final MachineRenderer rm = new MachineRenderer(item, clock,
		    new JPanel());
	    MachineRenderingController.getExecutor().execute(rm);
	    rendererMap.put(item, rm);
	}
	return rendererMap.get(item);
    }

}
