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

import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;

/**
 * @author triceo
 * 
 */
public class GroupRenderingController {

    private static final Integer MAX_THREADS = 10;
    private static ExecutorService e = null;

    private static Map<Integer, Map<MachineGroup, Future<JPanel>>> rendererOpen = new HashMap<Integer, Map<MachineGroup, Future<JPanel>>>();
    private static Map<Integer, Map<MachineGroup, Future<JPanel>>> rendererCollapsed = new HashMap<Integer, Map<MachineGroup, Future<JPanel>>>();

    private static ExecutorService getExecutor() {
	if (GroupRenderingController.e == null) {
	    GroupRenderingController.e = Executors
		    .newFixedThreadPool(GroupRenderingController.MAX_THREADS);
	}
	return GroupRenderingController.e;
    }

    private synchronized static Future<JPanel> getRenderer(
	    final MachineGroup item, final Integer clock,
	    final Map<Integer, Map<MachineGroup, Future<JPanel>>> storage) {
	if (!storage.containsKey(clock)) {
	    storage.put(clock, new HashMap<MachineGroup, Future<JPanel>>());
	}
	final Map<MachineGroup, Future<JPanel>> rendererMap = storage
		.get(clock);
	if (!rendererMap.containsKey(item)) {
	    final Callable<JPanel> gr = new GroupRenderer(item, clock,
		    new JPanel());
	    final Future<JPanel> future = GroupRenderingController
		    .getExecutor().submit(gr);
	    rendererMap.put(item, future);
	}
	return rendererMap.get(item);
    }

    public static Future<JPanel> getRendererCollapsed(final MachineGroup item,
	    final Integer clock) {
	return GroupRenderingController.getRenderer(item, clock,
		GroupRenderingController.rendererCollapsed);
    }

    public static Future<JPanel> getRendererOpen(final MachineGroup item,
	    final Integer clock) {
	return GroupRenderingController.getRenderer(item, clock,
		GroupRenderingController.rendererOpen);
    }

}
