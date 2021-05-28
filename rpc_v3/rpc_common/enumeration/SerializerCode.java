package rpc_common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializerCode {

    KRYO(0),
    HESSIAN(1),
    FASTJSON(2);

    private final int code;

}
