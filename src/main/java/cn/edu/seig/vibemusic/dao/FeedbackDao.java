package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * 反馈数据访问对象
 * 负责反馈相关的数据库操作
 */
public class FeedbackDao {

    /**
     * 添加反馈
     * @param userId 用户ID
     * @param content 反馈内容
     * @return 影响的行数
     */
    public int addFeedback(Long userId, String content) {
        String sql = "INSERT INTO tb_feedback (user_id, content, create_time) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, content);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


