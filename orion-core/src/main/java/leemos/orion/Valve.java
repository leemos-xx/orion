package leemos.orion;

import leemos.orion.core.engine.Invocation;
import leemos.orion.core.engine.RequestFaced;
import leemos.orion.core.engine.ResponseFaced;

/**
 * 业务处理单元
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public interface Valve {

    /**
     * 执行当前Valve
     *
     * @param request
     * @param response
     * @param invocation
     * @param chain
     */
    public void invoke(RequestFaced request, ResponseFaced response,
            Invocation invocation, ValveChain chain);
}
