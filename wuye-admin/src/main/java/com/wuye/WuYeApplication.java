package com.wuye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author wuye
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class WuYeApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(WuYeApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  物业报修系统启动成功   ლ(´ڡ`ლ)ﾞ)");
    }
}
