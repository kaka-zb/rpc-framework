package rpc_core.transport;

import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.SerializerCode;

import javax.xml.ws.Response;
import java.util.concurrent.CompletableFuture;

public interface RpcClient {

    int DEFAULT_SERIALIZER = SerializerCode.KRYO.getCode();

    CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest);

}
