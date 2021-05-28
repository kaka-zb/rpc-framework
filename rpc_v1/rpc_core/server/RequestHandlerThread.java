package rpc_core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_core.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest request = (RpcRequest) input.readObject();
            Object service = serviceRegistry.getService(request.getInterfaceName());

            Object result = null;
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            logger.info("服务：{} 成功调用方法：{}", request.getInterfaceName(), request.getMethodName());
            result =  method.invoke(service, request.getParameters());

            out.writeObject(RpcResponse.success(result));
            out.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
