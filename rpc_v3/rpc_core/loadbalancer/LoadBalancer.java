package rpc_core.loadbalancer;

import java.util.List;

public interface LoadBalancer {

    String select(List<String> addressList);

}
