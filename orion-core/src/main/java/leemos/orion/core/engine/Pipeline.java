package leemos.orion.core.engine;

import leemos.orion.Valve;

/**
 * 业务处理管道，管道中的业务处理单元为{@link Valve}
 * <ul>
 *   <li>所有请求在分发到最终的{@link Scene}和{@link Api}所标记的服务单元之前，都会统一先经过valve，
 *       因此valve中尽量用来处理一些公共的逻辑，比如日志、参数过滤、鉴权等</li>
 *   <li>先按序执行valves中的单元，最后执行basic单元</li>
 *   <li>类似J2EE中的filter chain</li>
 * </ul>
 *
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public interface Pipeline {

    /**
     * 获取basic valve
     *
     * @return
     */
    public Valve getBasic();

    /**
     * 设置basic valve
     *
     * @param basic
     */
    public void setBasic(Valve basic);

    /**
     * 添加Valve
     *
     * @param valve
     */
    public void addValve(Valve valve);

    /**
     * 获取所有Valve
     *
     * @return
     */
    public Valve[] getValves();

    /**
     * 删除Valve
     *
     * @param valve
     */
    public void removeValve(Valve valve);

    /**
     * 执行Valve链
     *
     * @param request
     * @param response
     * @param invocation
     */
    public void invoke(RequestFaced request, ResponseFaced response,
            Invocation invocation);
}
