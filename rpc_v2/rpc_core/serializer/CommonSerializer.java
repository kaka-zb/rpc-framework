package rpc_core.serializer;

public interface CommonSerializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new HessianSerializer();
            case 2:
                return new FastJsonSerializer();
            default:
                return null;
        }
    }
}
