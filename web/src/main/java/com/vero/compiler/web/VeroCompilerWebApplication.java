package com.vero.compiler.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;



/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 15:41 2018/1/22.
 * @since data-minning-platform
 */

@Configuration
@ComponentScan(basePackages = {"com.vero.compiler"}) // 扫描该包路径下的所有spring组件
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
public class VeroCompilerWebApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(VeroCompilerWebApplication.class, args);
    }
}
