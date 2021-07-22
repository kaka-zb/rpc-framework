package rpc_core.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.loadbalancer.LoadBalancer;
import rpc_core.loadbalancer.RandomLoadBalancer;
import rpc_core.registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.List;

public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private String zkAddress;
    private LoadBalancer loadBalancer = new RandomLoadBalancer();

    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public ZookeeperServiceDiscovery(String zkAddress, LoadBalancer loadBalancer) {
        this.zkAddress = zkAddress;
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        logger.info("connect zookeeper");

        try {
            // 根据 serviceName 查找 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

            // 查找 address 节点
            List<String> addressList = zkClient.getChildren(servicePath);
            if (addressList.isEmpty()) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

            // 若存在多个 address 节点，则通过负载均衡策略获取一个地址
            String address = loadBalancer.select(addressList);
            logger.info("get address node: {}", address);

            return zkClient.readData(servicePath + "/" + address);
        } finally {
            zkClient.close();
        }
    }
}
