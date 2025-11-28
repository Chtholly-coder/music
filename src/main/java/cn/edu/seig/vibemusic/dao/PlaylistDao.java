package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌单数据访问对象
 * 负责歌单相关的数据库操作
 */
public class PlaylistDao {

    /**
     * 分页查询歌单列表
     */
    public List<PlaylistVO> findByPage(int pageNum, int pageSize, String playlistName, String style) {
        List<PlaylistVO> playlists = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, name, cover_url, style, introduction, create_time FROM tb_playlist WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (playlistName != null && !playlistName.isEmpty()) {
            sql.append("AND name LIKE ? ");
            params.add("%" + playlistName + "%");
        }
        if (style != null && !style.isEmpty()) {
            sql.append("AND style LIKE ? ");
            params.add("%" + style + "%");
        }
        sql.append("ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNum - 1) * pageSize);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylistVO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    /**
     * 统计歌单数量
     */
    public long count(String playlistName, String style) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM tb_playlist WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (playlistName != null && !playlistName.isEmpty()) {
            sql.append("AND name LIKE ? ");
            params.add("%" + playlistName + "%");
        }
        if (style != null && !style.isEmpty()) {
            sql.append("AND style LIKE ? ");
            params.add("%" + style + "%");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取随机歌单
     */
    public List<PlaylistVO> findRandom(int limit) {
        List<PlaylistVO> playlists = new ArrayList<>();
        String sql = "SELECT id, name, cover_url, style, introduction, create_time FROM tb_playlist ORDER BY RAND() LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylistVO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    /**
     * 获取歌单详情（包含歌曲列表）
     */
    public PlaylistDetailVO findDetailById(Long playlistId) {
        String sql = "SELECT id, name, cover_url, style, introduction, create_time FROM tb_playlist WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlaylistDetailVO playlist = new PlaylistDetailVO();
                    playlist.setPlaylistId(rs.getLong("id"));
                    playlist.setPlaylistName(rs.getString("name"));
                    playlist.setCoverUrl(rs.getString("cover_url"));
                    playlist.setStyle(rs.getString("style"));
                    playlist.setIntroduction(rs.getString("introduction"));
                    Timestamp createTime = rs.getTimestamp("create_time");
                    if (createTime != null) {
                        playlist.setCreateTime(createTime.toLocalDateTime());
                    }

                    // 获取歌单的歌曲列表
                    playlist.setSongs(getSongsByPlaylistId(playlistId));
                    return playlist;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取歌单的歌曲列表
     */
    private List<SongDao.SongWithArtist> getSongsByPlaylistId(Long playlistId) {
        List<SongDao.SongWithArtist> songs = new ArrayList<>();
        String sql = "SELECT s.id, s.artist_id, s.name, s.album, s.duration, s.style, s.cover_url, s.audio_url, s.release_time, a.name as artist_name " +
                "FROM tb_song s " +
                "LEFT JOIN tb_artist a ON s.artist_id = a.id " +
                "INNER JOIN tb_playlist_binding pb ON s.id = pb.song_id " +
                "WHERE pb.playlist_id = ? ORDER BY pb.id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SongDao.SongWithArtist song = new SongDao.SongWithArtist();
                    song.setSongId(rs.getLong("id"));
                    song.setArtistId(rs.getLong("artist_id"));
                    song.setSongName(rs.getString("name"));
                    song.setAlbum(rs.getString("album"));
                    song.setDuration(rs.getString("duration"));
                    song.setStyle(rs.getString("style"));
                    song.setCoverUrl(rs.getString("cover_url"));
                    song.setAudioUrl(rs.getString("audio_url"));
                    song.setArtistName(rs.getString("artist_name"));
                    Date releaseTime = rs.getDate("release_time");
                    if (releaseTime != null) {
                        song.setReleaseTime(releaseTime.toLocalDate());
                    }
                    songs.add(song);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * 将ResultSet映射为PlaylistVO对象
     */
    private PlaylistVO mapResultSetToPlaylistVO(ResultSet rs) throws SQLException {
        PlaylistVO playlist = new PlaylistVO();
        playlist.setPlaylistId(rs.getLong("id"));
        playlist.setPlaylistName(rs.getString("name"));
        playlist.setCoverUrl(rs.getString("cover_url"));
        playlist.setStyle(rs.getString("style"));
        playlist.setIntroduction(rs.getString("introduction"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            playlist.setCreateTime(createTime.toLocalDateTime());
        }
        return playlist;
    }

    /**
     * 歌单VO类
     */
    public static class PlaylistVO {
        private Long playlistId;
        private String playlistName;
        private String coverUrl;
        private String style;
        private String introduction;
        private LocalDateTime createTime;
        private Integer likeStatus = 0;

        // Getter和Setter
        public Long getPlaylistId() { return playlistId; }
        public void setPlaylistId(Long playlistId) { this.playlistId = playlistId; }
        public String getPlaylistName() { return playlistName; }
        public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }
        public String getIntroduction() { return introduction; }
        public void setIntroduction(String introduction) { this.introduction = introduction; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public Integer getLikeStatus() { return likeStatus; }
        public void setLikeStatus(Integer likeStatus) { this.likeStatus = likeStatus; }
    }

    /**
     * 歌单详情VO类（包含歌曲列表）
     */
    public static class PlaylistDetailVO extends PlaylistVO {
        private List<SongDao.SongWithArtist> songs;

        public List<SongDao.SongWithArtist> getSongs() { return songs; }
        public void setSongs(List<SongDao.SongWithArtist> songs) { this.songs = songs; }
    }
}


