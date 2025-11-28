package cn.edu.seig.vibemusic.listener;

import cn.edu.seig.vibemusic.config.DatabaseConfig;
import cn.edu.seig.vibemusic.config.RedisConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * 应用上下文监听器
 * 用于在应用启动和关闭时执行初始化和清理操作
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 应用启动时执行
        System.out.println("Vibe Music Server 正在启动...");

        // 数据库连接池和Redis连接池会在首次使用时自动初始化
        // 这里可以添加其他初始化逻辑

        System.out.println("Vibe Music Server 启动完成!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 应用关闭时执行
        System.out.println("Vibe Music Server 正在关闭...");

        // 关闭数据库连接池
        DatabaseConfig.closeDataSource();
        System.out.println("数据库连接池已关闭");

        // 关闭Redis连接池
        RedisConfig.closePool();
        System.out.println("Redis连接池已关闭");

        System.out.println("Vibe Music Server 已关闭!");
    }
}


