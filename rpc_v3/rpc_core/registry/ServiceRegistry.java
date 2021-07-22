package rpc_core.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 注册服务名称与服务地址
     * @param serviceName 服务名称（被暴露的实现类的接口名称）
     * @param inetSocketAddress 服务地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
