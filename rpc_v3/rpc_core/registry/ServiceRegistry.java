package rpc_core.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
