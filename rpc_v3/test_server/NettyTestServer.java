package test_server;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import rpc_api.HelloService;
import rpc_core.transport.server.NettyServer;

public class NettyTestServer {

    public static void main(String[] args) {
        // 加载 Spring 配置文件
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
