package rpc_core.loadbalancer;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public String select(List<String> addressList) {
        if(index >= addressList.size()) {
            index %= addressList.size();
        }
        return addressList.get(index++);
    }

}
