package rpc_core.transport.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcError;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_core.loadbalancer.LoadBalancer;
import rpc_core.loadbalancer.RandomLoadBalancer;
import rpc_core.registry.ServiceDiscovery;
import rpc_core.registry.zookeeper.ZookeeperServiceDiscovery;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.RpcClient;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient(Integer serializer, LoadBalancer loadBalancer, String zkAddress) {
        this.serviceDiscovery = new ZookeeperServiceDiscovery(zkAddress, loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public NettyClient(String zkAddress) {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer(),zkAddress);
    }
    public NettyClient(LoadBalancer loadBalancer, String zkAddress) {
        this(DEFAULT_SERIALIZER, loadBalancer, zkAddress);
    }
    public NettyClient(Integer serializer, String zkAddress) {
        this(serializer, new RandomLoadBalancer(), zkAddress);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
