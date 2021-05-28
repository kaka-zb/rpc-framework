package rpc_core.server;

import javafx.concurrent.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private ExecutorService executorService;
    private final ServiceRegistry serviceRegistry;

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        executorService = Executors.newFixedThreadPool(10);
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动...");
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("客户端连接！{} : {}", socket.getInetAddress(), socket.getPort());
                executorService.execute(new RequestHandlerThread(socket, serviceRegistry));
            }
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生: ", e);
        }
    }
}
