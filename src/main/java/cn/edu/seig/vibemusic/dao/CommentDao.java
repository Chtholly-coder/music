package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * 评论数据访问对象
 * 负责评论相关的数据库操作
 */
public class CommentDao {

    /**
     * 新增歌曲评论
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @param content 评论内容
     * @return 影响的行数
     */
    public int addSongComment(Long userId, Long songId, String content) {
        String sql = "INSERT INTO tb_comment (user_id, song_id, type, content, like_count, create_time) VALUES (?, ?, 0, ?, 0, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, songId);
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 新增歌单评论
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @param content 评论内容
     * @return 影响的行数
     */
    public int addPlaylistComment(Long userId, Long playlistId, String content) {
        String sql = "INSERT INTO tb_comment (user_id, playlist_id, type, content, like_count, create_time) VALUES (?, ?, 1, ?, 0, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 影响的行数
     */
    public int likeComment(Long commentId) {
        String sql = "UPDATE tb_comment SET like_count = like_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return 影响的行数
     */
    public int cancelLikeComment(Long commentId) {
        String sql = "UPDATE tb_comment SET like_count = GREATEST(like_count - 1, 0) WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 影响的行数
     */
    public int deleteComment(Long commentId) {
        String sql = "DELETE FROM tb_comment WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


