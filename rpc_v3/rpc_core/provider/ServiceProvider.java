package rpc_core.provider;

public interface ServiceProvider {

    <T> void addServiceProvider(T service, Class<T> serviceClass);

    Object getServiceProvider(String serviceName);

}
