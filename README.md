# rpc-framework

基于 Nacos + Netty 实现的简易 rpc 框架，从最简单的 v1 版本，不断完善其功能直至 v3 版本。



## 版本描述

rpc_v1：使用 JDK 序列化和 Socket 实现的一个基本 RPC 框架，使用一个 Map 作为服务注册中心

rpc_v2：将 v1 版本的 BIO 传输方式替换为使用 Netty 的 NIO 传输方式

rpc_v3：将 v1 版本的 Map 注册中心改为使用 Nacos 作为服务注册中心



## 已完成部分

使用 Netty 进行网络传输

实现 Netty 心跳机制：保证客户端和服务端的连接不被断掉，避免重连

客户端与服务端通信协议（数据包结构）重新设计

实现了三种序列化方式：Kryo，Hession，Fastjson

使用 Nacao 作为注册中心，管理相关服务地址信息

客户端调用远程服务的时候进行负载均衡，实现了随机算法和轮转算法





