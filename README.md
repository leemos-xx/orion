# orion

orion是一个轻量级的rpc框架，用于学习交流目的。

## 1. 核心功能
* 多种网络通讯模式：单向、同步、异步、异步回调；
* 网络连接管理：管理C端与P端的通讯链路，长连接情况下支持心跳、健康检查与自动重连；
* 协议定制扩展：默认支持基于自定义的orion协议以及标准的http协议实现rpc，也支持扩展其它协议；
* 序列化扩展：默认支持hessian，支持扩展其它序列化机制；

## 2. 快速开始
### 2.1 pom.xml
orion相关依赖，目前支持标准的http协议（`orion-connector-hrpc`）和自定义的orion协议（`orion-connector-rpc`），可以根据自己的需要引入。
```xml
<dependency>
  <groupId>leemos</groupId>
  <artifactId>orion-connector-hrpc</artifactId>
</dependency>
<dependency>
  <groupId>leemos</groupId>
  <artifactId>orion-connector-rpc</artifactId>
</dependency>
```

### 2.1 Server
启动Server端的代码很简单，只要声明`StandardServer`的实例，然后`start()`即可。
Server端的代码可以参考`orion-example-server`项目。
```java
public class ExampleServer {
    private Server server;
    private CountDownLatch latch = new CountDownLatch(1);

    public ExampleServer() {
    }

    public static void main(String[] args) throws LifecycleException {
        new ExampleServer().start();
    }

    public void start() throws LifecycleException {
        server = new StandardServer();
        try {
            server.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    public void stop() throws LifecycleException {
        try {
            server.stop();
        } finally {
            latch.countDown();
        }
    }
}
```

### 2.2 Server端业务实现
以简单的支付业务为例，首先声明实体。
```java
public class PayRequest implements Serializable {
    private String payAcct;
    private String recvAcct;
    private double amount;
}

public class PayResponse implements Serializable {
    private boolean status;
    private String message;
}
```

在Server端进行业务逻辑实现，其中关键的是`Scene`和`Api`注解，通过这两个注解，可以声明我们要解决的问题场景和具体的Api。
```java
@Scene(name = "example")
public class ExampleScene {

    @Api(name = "pay")
    public PayResponse pay(PayRequest request) {
        System.out.println("payment...");
        System.out.println("PayAcct: " + request.getPayAcct());
        System.out.println("RecvAcct: " + request.getRecvAcct());
        System.out.println("Amount: " + request.getAmount());

        return PayResponse.success();
    }

}
```

### 2.3 Client
Client通过对接口添加`ServiceInvocation`注解来声明要调用的方法，其中`ServiceInvocation`中的`name`字段应该与Server中的`Scene`和`Api`注解的值映射上。
Client端的代码可以参考`orion-example-client`项目。
```java
public interface Pay {
    @ServiceInvocation(name = "example.pay")
    public PayResponse pay(PayRequest request);
}
```

Client端进行调用服务。
```java
public class ExampleClient {

    public static void main(String[] args) throws LifecycleException {
        OrionClient client = new OrionClient();
        client.start();

        Proxy proxy = new JdkProxy();
        ProxyInvoker proxyInvoker = new JdkProxyInvoker("127.0.0.1:10880", client);

        // 获取代理后的Pay
        Pay payScene = proxy.getProxy(Pay.class, proxyInvoker);

        // 进行rpc远程调用并处理结果
        PayResponse response = payScene.pay(new PayRequest("62250000", "62251100", 10.24));
        if (response.isStatus()) {
            System.out.println("successed");
        } else {
            System.out.println("failed: " + response.getMessage());
        }

        client.stop();
    }

}
```

### 2.4 结果
运行仓库中的`orion-example-client`和`orion-example-server`项目，如果看到如下输出，则说明rpc调用成功。
```java
// server
2022-02-19 17:06:47.814 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.valves.LogValve - Business request message received, id=1, serivce=example.pay, parameters=[{"amount":10.24,"payAcct":"62250000","recvAcct":"62251100"}]
2022-02-19 17:06:47.815 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.example.scene.ExampleScene - payment...
2022-02-19 17:06:47.816 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.example.scene.ExampleScene - PayAcct: 62250000
2022-02-19 17:06:47.816 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.example.scene.ExampleScene - RecvAcct: 62251100
2022-02-19 17:06:47.816 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.example.scene.ExampleScene - Amount: 10.24
2022-02-19 17:06:47.818 [StandardEngine["orion-engine"]-biz-4-thread-1] [INFO ] leemos.orion.valves.LogValve - Business response message sended, id=1, status=SUCCESS, body={"status":true}

// client
17:06:47.672 [orion-client-io-1-thread-1] DEBUG leemos.orion.remoting.BaseRemoting - Request message send success, id=1
17:06:47.909 [orion-client-io-1-thread-1] DEBUG leemos.orion.rpc.RpcClientHandler - Response message received, id=1, status=SUCCESS, body={"status":true}
17:06:47.909 [main] INFO leemos.orion.example.ExampleClient - successed
```

## 3. 配置项
```yml
projectName: orion-sample
projectVersion: 1.0
serverName: orion-server
adminPort: 10615
services: 
 - name: orion-service
   eventBus: 
      !leemos.orion.config.QueueEventBusConfig
      type: queued
      name: orion-event-bus
      eventQueuedSize: 2000
      discardTimeoutMillis: 5000
   engine:
      name: orion-engine
   connectors:
    - name: orion-connector
      protocol: leemos.orion.connector.rpc.OrionConnector
      ip: 127.0.0.1
      port: 10880
      ioThreads: 8
```

## 4. 概要设计
### 4.1 orion协议
```java
0      1      2      3      4      5      6      7      8      9     10     11     12     13     14     15     16 byte
+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+------+
| magic|  ver |marker|status|                       messageId                       |        dataLength         |
+------+------+------+------+-------------------------------------------------------+---------------------------+
|                                                 content bytes                                                 |
+                                                                                                               |
|                                                     ......                        |           CRC32           |
+---------------------------------------------------------------------------------------------------------------+

about marker byte：
0      1      2      3      4      5      6      7      8 bit
+------+------+------+------+------+------+------+------+
| rq/rp|oneway|  hb  |  crc |       serialization       |
+------+------+------+------+---------------------------+
```

### 4.2 功能说明
![功能说明](https://images.gitee.com/uploads/images/2020/0912/112022_7ac5c7b2_7580843.png "2020-09-12 11-19-39屏幕截图.png")

### 4.3 内部逻辑
![内部逻辑](https://images.gitee.com/uploads/images/2020/0912/112310_16b02055_7580843.png "2020-09-12 11-22-37屏幕截图.png")

### 4.4 orion UML
![uml](https://images.gitee.com/uploads/images/2020/0821/164833_a9d3c59d_7580843.png "orion.png")

### 4.5 orion client同步调用流程设计：
![client](https://images.gitee.com/uploads/images/2020/0821/164919_8db86069_7580843.png "client-call.png")

### 4.6 线程模型
![thead model](https://images.gitee.com/uploads/images/2020/0805/155229_2ce4d3a6_7580843.png "2020-08-05 15-51-13屏幕截图.png")

### 4.7 压测数据
![cpu](https://images.gitee.com/uploads/images/2020/0907/155329_8175637a_7580843.png "cpu.png")
![tps](https://images.gitee.com/uploads/images/2020/0907/155338_7e6ea80b_7580843.png "tps.png")
![rsp](https://images.gitee.com/uploads/images/2020/0907/155347_81269955_7580843.png "rsp-time.png") 
