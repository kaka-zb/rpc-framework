package test_client;

import rpc_api.HelloObject;
import rpc_api.HelloService;
import rpc_core.transport.RpcClient;
import rpc_core.transport.RpcClientProxy;
import rpc_core.transport.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is zhangbo");
        String ans = helloService.hello(object);
        System.out.println(ans);
    }
}
