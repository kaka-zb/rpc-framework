package rpc_core.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.SerializerCode;

public class FastJsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(FastJsonSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.config(SerializerFeature.WriteClassName, true);
        serializer.write(obj);
        return out.toBytes("UTF-8");
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ParserConfig.getGlobalInstance().addAccept("com.taobao.pac.client.sdk.dataobject.");
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

        return JSON.parseObject(new String(bytes), clazz);
    }

    @Override
    public int getCode() {
        return SerializerCode.FASTJSON.getCode();
    }
}
