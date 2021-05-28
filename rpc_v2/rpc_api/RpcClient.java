package rpc_api;

import rpc_common.entity.RpcRequest;

public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

}
