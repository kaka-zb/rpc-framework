package rpc_core.provider;

import java.util.Map;

public interface ServiceProvider {

    <T> void addServiceProvider(Object service, Class<?> serviceClass);

    Object getServiceProvider(String serviceName);

    Map<String, Object> getServiceMap();

}
