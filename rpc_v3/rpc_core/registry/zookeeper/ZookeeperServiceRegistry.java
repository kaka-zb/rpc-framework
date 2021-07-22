package rpc_core.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.registry.ServiceRegistry;

import java.net.InetSocketAddress;

public class ZookeeperServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    // zookeeper 客户端 ZkClient
    private ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        logger.info("connect zookeeper");
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        // 创建 registry 持久节点，该节点下存放所有的 service 节点
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            logger.info("create registry node: {}", registryPath);
        }

        // 在 registry 节点下创建 service 持久节点，存放服务名称
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            logger.info("create service node: {}", servicePath);
        }

        // 在 service 节点下创建 address 临时节点,存放服务地址
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, inetSocketAddress);
        logger.info("create address node: {}", addressNode);
    }
}
