package leemos.orion.client;

import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

public interface ProxyInvoker {

    RpcResponse invoke(RequestBody request);

}
