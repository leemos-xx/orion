package leemos.orion.valves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import leemos.orion.Constants;
import leemos.orion.EventBus;
import leemos.orion.ValveChain;
import leemos.orion.commons.StringManager;
import leemos.orion.core.engine.Invocation;
import leemos.orion.core.engine.RequestFaced;
import leemos.orion.core.engine.ResponseFaced;
import leemos.orion.remoting.Status;

/**
 * 日志记录的业务处理单元
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class LogValve extends ValveBase {

    private static final Logger logger = LoggerFactory.getLogger(LogValve.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    public LogValve(EventBus eventBus) {
        super(eventBus);
    }

    /*
     * @see leemos.orion.Valve#invoke(leemos.orion.RequestFaced,
     * leemos.orion.ResponseFaced, leemos.orion.Invocation,
     * leemos.orion.ValveChain)
     */
    @Override
    public void invoke(RequestFaced request, ResponseFaced response,
            Invocation invocation, ValveChain chain) {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("logValve.invoke.request",
                    new String[] { String.valueOf(request.getId()),
                            request.getServiceName(),
                            request.getParameters() == null ? "[]"
                                    : JSONArray.toJSONString(request.getParameters()) }));
        }
        chain.invokeNext(request, response, invocation);

        if (logger.isInfoEnabled()) {
            if (response.getStatus() != null) {
                if (response.getStatus() == Status.SUCCESS) {
                    logger.info(sm.getString("logValve.invoke.response", new String[] {
                            String.valueOf(request.getId()), response.getStatus().name(),
                            response.getResponseBody() == null ? ""
                                    : JSONArray
                                            .toJSONString(response.getResponseBody()) }));
                } else {
                    logger.info(sm.getString("logValve.invoke.response",
                            new String[] { String.valueOf(request.getId()),
                                    response.getStatus().name(), "" }));
                }
            }
        }

    }

}
