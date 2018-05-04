package OLink.bpm.core.workflow.engine.state;

import java.lang.reflect.Constructor;

import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.State;
import org.apache.log4j.Logger;

public class StateCreator {
	static Logger LOG = Logger.getLogger(StateCreator.class);
	
	/**
	 * 获取节点状态实例
	 * 
	 * @param node
	 *            流程节点
	 * @return
	 * @throws Exception
	 */
	public static State getNodeState(Node node) {
		try {
			if (node != null) {
				String simpleName = node.getClass().getSimpleName();
				if (simpleName.indexOf("Node") != -1) {
					simpleName = simpleName.substring(0, simpleName.indexOf("Node"));
				}
				Class<?> stateClass = Class.forName("OLink.bpm.core.workflow.engine.state." + simpleName + "State");
				Constructor<?> constructor = stateClass.getConstructor(Node.class);
				State state = (State) constructor.newInstance(node);

				return state;
			} else {
				return new NullState(null);
			}
		} catch (Exception e) {
			LOG.warn("getNodeState", e);
			return new NullState(node);
		}
	}
}
