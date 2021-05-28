package test_server;

import rpc_api.HelloService;
import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;
import rpc_core.server.RpcServer;

public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        RpcServer server = new RpcServer(registry);
        server.start(9000);

    }
}
