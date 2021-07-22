package rpc_core.transport.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 定义服务暴露注解 @RpcService 供用户使用
 * 考虑一个类有多个接口的情况
 * 暂不考虑一个接口有多个实现类的情况（可以用version版本号解决）
 */
@Target(ElementType.TYPE) // 表示 @RpcService 注解可放在 接口、类、枚举、注解 上
@Retention(RetentionPolicy.RUNTIME) // 表示 @RpcService 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Component // 表明 @RpcService 注解可被 Spring 扫描
public @interface RpcService {

    // 服务类型（被暴露的实现类的接口类型）
    Class<?> interfaceName();

}
