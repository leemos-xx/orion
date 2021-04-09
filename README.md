# orion

orion是一个轻量级的rpc框架，代码易于理解、注释完善、风格统一，希望能对初学rpc的朋友有所助益。

## 1. 核心功能
* 多种网络通讯模式：单向、同步、异步、异步回调；
* 网络连接管理：管理C端与P端的通讯链路，长连接情况下支持心跳、健康检查与自动重连；
* 协议定制扩展：默认支持基于自定义的orion协议以及标准的http协议实现rpc，也支持扩展其它协议；
* 序列化扩展：默认支持hessian，支持扩展其它序列化机制；

## 2. 快速开始

## 3. 配置项
yml

## 4. 




#### orion引擎
orion 引擎部分概要功能和结构设计如下图所示：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0912/112022_7ac5c7b2_7580843.png "2020-09-12 11-19-39屏幕截图.png")

orion 内部数据流：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0912/112310_16b02055_7580843.png "2020-09-12 11-22-37屏幕截图.png")

orion 引擎部分UML类图设计如下所示：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0821/164833_a9d3c59d_7580843.png "orion.png")

orion client同步调用流程设计：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0821/164919_8db86069_7580843.png "client-call.png")

orion 线程模型如下图所示：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0805/155229_2ce4d3a6_7580843.png "2020-08-05 15-51-13屏幕截图.png")

8C单机初步压测结果：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0907/155329_8175637a_7580843.png "cpu.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0907/155338_7e6ea80b_7580843.png "tps.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0907/155347_81269955_7580843.png "rsp-time.png") **粗体** 