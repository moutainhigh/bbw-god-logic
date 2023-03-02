package com.bbw;

import com.bbw.db.datasources.DynamicDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * 启动类
 * Aop说明：
 * proxyTargetClass的赋值来决定Spring AOP动态代理机制。
 * proxyTargetClass为false时，JDK 动态代理是利用反射机制生成一个实现代理接口的类，在调用具体方法前调用InvokeHandler来处理。
 * proxyTargetClass 为 true时，CGlib 动态代理是利用ASM（开源的Java字节码编辑库，操作字节码）开源包，将代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。
 * 区别是JDK代理只能对实现接口的类生成代理；CGlib是针对类实现代理，对指定的类生成一个子类，并覆盖其中的方法，这种通过继承类的实现方式，不能代理final修饰的类。
 */
@Configuration
@MapperScan(basePackages = {"com.bbw.*.dao", "com.bbw.god.*.dao"})
@Import({DynamicDataSourceConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LogicServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LogicServerApplication.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
