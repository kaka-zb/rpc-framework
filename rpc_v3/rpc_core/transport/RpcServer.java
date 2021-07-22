package rpc_core.transport;

import rpc_common.enumeration.SerializerCode;

public interface RpcServer {

    int DEFAULT_SERIALIZER = SerializerCode.KRYO.getCode();


}
