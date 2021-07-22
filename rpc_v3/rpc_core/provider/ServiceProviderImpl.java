package rpc_core.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcError;
import rpc_common.exception.RpcException;
import rpc_core.transport.server.RpcService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将每一个server能提供的接口服务注册在这里
 * 使用一个map保存服务名称和对应的实现类
 * 但是一个服务只能有一个实现类，多的会覆盖掉
 */

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(Object service, Class<?> serviceClass) {
        RpcService rpcService = service.getClass().getAnnotation(RpcService.class);
        String serviceName = rpcService.interfaceName().getName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }
}
