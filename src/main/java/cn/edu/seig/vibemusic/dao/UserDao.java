package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;
import cn.edu.seig.vibemusic.model.entity.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 * 负责用户相关的数据库操作
 */
public class UserDao {

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象，不存在返回null
     */
    public User findByEmail(String email) {
        String sql = "SELECT id, username, password, phone, email, user_avatar, introduction, create_time, update_time, status FROM tb_user WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, phone, email, user_avatar, introduction, create_time, update_time, status FROM tb_user WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户对象，不存在返回null
     */
    public User findById(Long userId) {
        String sql = "SELECT id, username, password, phone, email, user_avatar, introduction, create_time, update_time, status FROM tb_user WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    public int insert(User user) {
        String sql = "INSERT INTO tb_user (username, password, email, create_time, update_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.valueOf(user.getCreateTime()));
            ps.setTimestamp(5, Timestamp.valueOf(user.getUpdateTime()));
            ps.setInt(6, user.getUserStatus());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    public int update(User user) {
        String sql = "UPDATE tb_user SET username = ?, phone = ?, email = ?, introduction = ?, update_time = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getIntroduction());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(6, user.getUserId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 影响的行数
     */
    public int updateAvatar(Long userId, String avatarUrl) {
        String sql = "UPDATE tb_user SET user_avatar = ?, update_time = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarUrl);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码（已加密）
     * @return 影响的行数
     */
    public int updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE tb_user SET password = ?, update_time = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据邮箱更新密码
     * @param email 邮箱
     * @param newPassword 新密码（已加密）
     * @return 影响的行数
     */
    public int updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE tb_user SET password = ?, update_time = ? WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, email);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    public int deleteById(Long userId) {
        String sql = "DELETE FROM tb_user WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取用户总数
     * @return 用户总数
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM tb_user";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 用户列表
     */
    public List<User> findByPage(int pageNum, int pageSize) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, phone, email, user_avatar, introduction, create_time, update_time, status FROM tb_user ORDER BY create_time DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (pageNum - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet对象
     * @return User对象
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setUserAvatar(rs.getString("user_avatar"));
        user.setIntroduction(rs.getString("introduction"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            user.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            user.setUpdateTime(updateTime.toLocalDateTime());
        }
        user.setUserStatus(rs.getInt("status"));
        return user;
    }
}


