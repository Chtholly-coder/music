package cn.edu.seig.vibemusic.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库配置类
 * 使用HikariCP连接池管理数据库连接
 */
public class DatabaseConfig {

    // 单例数据源
    private static HikariDataSource dataSource;

    // 数据库配置参数
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vibe_music?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Kishi...015"; // 请修改为你的MySQL密码
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // 静态初始化块，创建数据源
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC_URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setDriverClassName(DRIVER_CLASS);

            // 连接池配置
            config.setMaximumPoolSize(10);          // 最大连接数
            config.setMinimumIdle(5);               // 最小空闲连接数
            config.setIdleTimeout(300000);          // 空闲超时时间（5分钟）
            config.setConnectionTimeout(20000);     // 连接超时时间（20秒）
            config.setMaxLifetime(1200000);         // 连接最大生命周期（20分钟）

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败", e);
        }
    }

    /**
     * 获取数据源
     * @return 数据源对象
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭数据源
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}


