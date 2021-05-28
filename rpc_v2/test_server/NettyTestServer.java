package test_server;

import rpc_api.HelloService;
import rpc_core.netty.server.NettyServer;
import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
