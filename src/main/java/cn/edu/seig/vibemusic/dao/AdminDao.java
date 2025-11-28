package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;
import cn.edu.seig.vibemusic.model.entity.Admin;

import java.sql.*;

/**
 * 管理员数据访问对象
 * 负责管理员相关的数据库操作
 */
public class AdminDao {

    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员对象，不存在返回null
     */
    public Admin findByUsername(String username) {
        String sql = "SELECT id, username, password FROM tb_admin WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ID查询管理员
     * @param adminId 管理员ID
     * @return 管理员对象，不存在返回null
     */
    public Admin findById(Long adminId) {
        String sql = "SELECT id, username, password FROM tb_admin WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将ResultSet映射为Admin对象
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getLong("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        return admin;
    }

    /**
     * 插入新管理员
     * @param username 用户名
     * @param password 密码（已加密）
     * @return 影响的行数
     */
    public int insert(String username, String password) {
        String sql = "INSERT INTO tb_admin (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

