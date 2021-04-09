package leemos.orion.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import leemos.orion.Valve;
import leemos.orion.ValveChain;
import leemos.orion.core.engine.Invocation;
import leemos.orion.core.engine.Pipeline;
import leemos.orion.core.engine.RequestFaced;
import leemos.orion.core.engine.ResponseFaced;
import leemos.orion.core.engine.StandardPipeline;

public class StandardPipelineTestCase {

    private int i = 1;

    @Test
    public void testPipeline() {
        Pipeline pipeline = new StandardPipeline();

        pipeline.setBasic(new Valve() {
            @Override
            public void invoke(RequestFaced request, ResponseFaced response,
                    Invocation invocation, ValveChain chain) {
                assertEquals(3, i++);
                chain.invokeNext(request, response, invocation);
            }
        });

        pipeline.addValve(new Valve() {
            @Override
            public void invoke(RequestFaced request, ResponseFaced response,
                    Invocation invocation, ValveChain chain) {
                assertEquals(1, i++);
                chain.invokeNext(request, response, invocation);
            }
        });

        pipeline.addValve(new Valve() {
            @Override
            public void invoke(RequestFaced request, ResponseFaced response,
                    Invocation invocation, ValveChain chain) {
                assertEquals(2, i++);
                chain.invokeNext(request, response, invocation);
            }
        });

        pipeline.invoke(null, null, null);
    }
}
