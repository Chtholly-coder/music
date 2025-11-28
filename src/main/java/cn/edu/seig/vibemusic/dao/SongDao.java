package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;
import cn.edu.seig.vibemusic.model.entity.Song;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲数据访问对象
 * 负责歌曲相关的数据库操作
 */
public class SongDao {

    /**
     * 根据ID查询歌曲
     * @param songId 歌曲ID
     * @return 歌曲对象，不存在返回null
     */
    public Song findById(Long songId) {
        String sql = "SELECT id, artist_id, name, album, lyric, duration, style, cover_url, audio_url, release_time FROM tb_song WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, songId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSong(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分页查询歌曲列表（带歌手名称）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param songName 歌曲名（模糊查询，可为null）
     * @param artistName 歌手名（模糊查询，可为null）
     * @param album 专辑名（模糊查询，可为null）
     * @return 歌曲列表
     */
    public List<SongWithArtist> findByPage(int pageNum, int pageSize, String songName, String artistName, String album) {
        List<SongWithArtist> songs = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.id, s.artist_id, s.name, s.album, s.duration, s.style, s.cover_url, s.audio_url, s.release_time, a.name as artist_name ");
        sql.append("FROM tb_song s LEFT JOIN tb_artist a ON s.artist_id = a.id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (songName != null && !songName.isEmpty()) {
            sql.append("AND s.name LIKE ? ");
            params.add("%" + songName + "%");
        }
        if (artistName != null && !artistName.isEmpty()) {
            sql.append("AND a.name LIKE ? ");
            params.add("%" + artistName + "%");
        }
        if (album != null && !album.isEmpty()) {
            sql.append("AND s.album LIKE ? ");
            params.add("%" + album + "%");
        }
        sql.append("ORDER BY s.id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNum - 1) * pageSize);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapResultSetToSongWithArtist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * 统计歌曲数量
     * @param songName 歌曲名（模糊查询，可为null）
     * @param artistName 歌手名（模糊查询，可为null）
     * @param album 专辑名（模糊查询，可为null）
     * @return 歌曲数量
     */
    public long count(String songName, String artistName, String album) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM tb_song s LEFT JOIN tb_artist a ON s.artist_id = a.id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (songName != null && !songName.isEmpty()) {
            sql.append("AND s.name LIKE ? ");
            params.add("%" + songName + "%");
        }
        if (artistName != null && !artistName.isEmpty()) {
            sql.append("AND a.name LIKE ? ");
            params.add("%" + artistName + "%");
        }
        if (album != null && !album.isEmpty()) {
            sql.append("AND s.album LIKE ? ");
            params.add("%" + album + "%");
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
     * 获取随机推荐歌曲
     * @param limit 数量限制
     * @return 歌曲列表
     */
    public List<SongWithArtist> findRandom(int limit) {
        List<SongWithArtist> songs = new ArrayList<>();
        String sql = "SELECT s.id, s.artist_id, s.name, s.album, s.duration, s.style, s.cover_url, s.audio_url, s.release_time, a.name as artist_name " +
                "FROM tb_song s LEFT JOIN tb_artist a ON s.artist_id = a.id ORDER BY RAND() LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapResultSetToSongWithArtist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * 获取歌曲详情（包含歌手信息）
     * @param songId 歌曲ID
     * @return 歌曲详情
     */
    public SongWithArtist findDetailById(Long songId) {
        String sql = "SELECT s.id, s.artist_id, s.name, s.album, s.lyric, s.duration, s.style, s.cover_url, s.audio_url, s.release_time, a.name as artist_name " +
                "FROM tb_song s LEFT JOIN tb_artist a ON s.artist_id = a.id WHERE s.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, songId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SongWithArtist song = mapResultSetToSongWithArtist(rs);
                    song.setLyric(rs.getString("lyric"));
                    return song;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将ResultSet映射为Song对象
     */
    private Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getLong("id"));
        song.setArtistId(rs.getLong("artist_id"));
        song.setSongName(rs.getString("name"));
        song.setAlbum(rs.getString("album"));
        song.setLyric(rs.getString("lyric"));
        song.setDuration(rs.getString("duration"));
        song.setStyle(rs.getString("style"));
        song.setCoverUrl(rs.getString("cover_url"));
        song.setAudioUrl(rs.getString("audio_url"));
        Date releaseTime = rs.getDate("release_time");
        if (releaseTime != null) {
            song.setReleaseTime(releaseTime.toLocalDate());
        }
        return song;
    }

    /**
     * 将ResultSet映射为SongWithArtist对象
     */
    private SongWithArtist mapResultSetToSongWithArtist(ResultSet rs) throws SQLException {
        SongWithArtist song = new SongWithArtist();
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
        return song;
    }

    /**
     * 歌曲带歌手名称的VO类
     */
    public static class SongWithArtist {
        private Long songId;
        private Long artistId;
        private String songName;
        private String artistName;
        private String album;
        private String lyric;
        private String duration;
        private String style;
        private String coverUrl;
        private String audioUrl;
        private LocalDate releaseTime;
        private Integer likeStatus = 0; // 默认未收藏

        // Getter和Setter方法
        public Long getSongId() { return songId; }
        public void setSongId(Long songId) { this.songId = songId; }
        public Long getArtistId() { return artistId; }
        public void setArtistId(Long artistId) { this.artistId = artistId; }
        public String getSongName() { return songName; }
        public void setSongName(String songName) { this.songName = songName; }
        public String getArtistName() { return artistName; }
        public void setArtistName(String artistName) { this.artistName = artistName; }
        public String getAlbum() { return album; }
        public void setAlbum(String album) { this.album = album; }
        public String getLyric() { return lyric; }
        public void setLyric(String lyric) { this.lyric = lyric; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        public LocalDate getReleaseTime() { return releaseTime; }
        public void setReleaseTime(LocalDate releaseTime) { this.releaseTime = releaseTime; }
        public Integer getLikeStatus() { return likeStatus; }
        public void setLikeStatus(Integer likeStatus) { this.likeStatus = likeStatus; }
    }
}


