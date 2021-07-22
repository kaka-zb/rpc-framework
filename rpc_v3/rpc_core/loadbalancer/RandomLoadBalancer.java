package rpc_core.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public String select(List<String> addressList) {
        return addressList.get(new Random().nextInt(addressList.size()));
    }
}
