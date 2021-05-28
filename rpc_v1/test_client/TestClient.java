package test_client;

import rpc_api.HelloObject;
import rpc_api.HelloService;
import rpc_core.client.RpcClientProxy;

public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "nice to meet you!");
        System.out.println(helloService.hello(object));
    }
}
