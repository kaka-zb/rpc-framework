package rpc_core.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.provider.ServiceProvider;
import rpc_core.provider.ServiceProviderImpl;
import rpc_core.registry.ServiceRegistry;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.RpcServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NettyServer implements RpcServer, ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private final CommonSerializer serializer;

    public NettyServer(String host, int port, ServiceRegistry serviceRegistry) {
        this(host, port, DEFAULT_SERIALIZER, serviceRegistry);
    }

    public NettyServer(String host, int port, Integer serializer, ServiceRegistry serviceRegistry) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = serviceRegistry;
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    /**
     * Spring 容器在加载的时候会自动调用一次 setApplicationContext, 并将上下文 ApplicationContext 传递给这个方法
     * 也就是说在 server 启动时该方法就会进行调用
     * 该方法的作用就是获取带有 @RpcService 注解的类的 value (被暴露的实现类的接口名称) 和 version (被暴露的实现类的版本号，默认为 “”)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                serviceProvider.addServiceProvider(serviceBean, serviceBean.getClass());
            }
        }
    }

    /**
     * 在初始化 Bean 的时候会自动执行该方法
     * 该方法的目标就是启动 Netty 服务器进行服务端和客户端的通信，接收并处理客户端发来的请求,
     * 并且还要将服务名称和服务地址注册进注册中心
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast((ChannelHandler) new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();

            // 注册服务
            if (serviceRegistry != null) {
                for (String serviceName : serviceProvider.getServiceMap().keySet()) {
                    serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
                }
            }

            // 阻塞等待直到服务器的 channel 关闭，否则会直接进入 finally 代码块
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
