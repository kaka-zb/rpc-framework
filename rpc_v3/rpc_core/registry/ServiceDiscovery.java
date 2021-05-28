package rpc_core.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
