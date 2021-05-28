package test_server;

import rpc_api.HelloService;
import rpc_core.transport.server.NettyServer;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.publishService(helloService, HelloService.class);
        server.start();
    }
}
