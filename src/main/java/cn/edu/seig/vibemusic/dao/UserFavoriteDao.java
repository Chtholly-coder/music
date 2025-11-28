package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户收藏数据访问对象
 * 负责用户收藏相关的数据库操作
 */
public class UserFavoriteDao {

    /**
     * 获取用户收藏的歌曲ID集合
     * @param userId 用户ID
     * @return 歌曲ID集合
     */
    public Set<Long> getFavoriteSongIds(Long userId) {
        Set<Long> songIds = new HashSet<>();
        String sql = "SELECT song_id FROM tb_user_favorite WHERE user_id = ? AND type = 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songIds.add(rs.getLong("song_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songIds;
    }

    /**
     * 获取用户收藏的歌单ID集合
     * @param userId 用户ID
     * @return 歌单ID集合
     */
    public Set<Long> getFavoritePlaylistIds(Long userId) {
        Set<Long> playlistIds = new HashSet<>();
        String sql = "SELECT playlist_id FROM tb_user_favorite WHERE user_id = ? AND type = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playlistIds.add(rs.getLong("playlist_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistIds;
    }

    /**
     * 判断歌曲是否被用户收藏
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return true-已收藏，false-未收藏
     */
    public boolean isSongFavorite(Long userId, Long songId) {
        String sql = "SELECT COUNT(*) FROM tb_user_favorite WHERE user_id = ? AND song_id = ? AND type = 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, songId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断歌单是否被用户收藏
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @return true-已收藏，false-未收藏
     */
    public boolean isPlaylistFavorite(Long userId, Long playlistId) {
        String sql = "SELECT COUNT(*) FROM tb_user_favorite WHERE user_id = ? AND playlist_id = ? AND type = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 收藏歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return 影响的行数
     */
    public int collectSong(Long userId, Long songId) {
        String sql = "INSERT INTO tb_user_favorite (user_id, song_id, type, create_time) VALUES (?, ?, 0, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, songId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 取消收藏歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return 影响的行数
     */
    public int cancelCollectSong(Long userId, Long songId) {
        String sql = "DELETE FROM tb_user_favorite WHERE user_id = ? AND song_id = ? AND type = 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, songId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 收藏歌单
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @return 影响的行数
     */
    public int collectPlaylist(Long userId, Long playlistId) {
        String sql = "INSERT INTO tb_user_favorite (user_id, playlist_id, type, create_time) VALUES (?, ?, 1, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 取消收藏歌单
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @return 影响的行数
     */
    public int cancelCollectPlaylist(Long userId, Long playlistId) {
        String sql = "DELETE FROM tb_user_favorite WHERE user_id = ? AND playlist_id = ? AND type = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


