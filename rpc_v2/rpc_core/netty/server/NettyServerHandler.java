package rpc_core.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        logger.info("服务器接收到请求: {}", rpcRequest);
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        Object result = requestHandler.handle(rpcRequest, service);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
