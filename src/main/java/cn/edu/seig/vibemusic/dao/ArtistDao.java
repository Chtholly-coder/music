package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌手数据访问对象
 * 负责歌手相关的数据库操作
 */
public class ArtistDao {

    /**
     * 分页查询歌手列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param artistName 歌手名（模糊查询，可为null）
     * @param gender 性别（可为null）
     * @param area 地区（可为null）
     * @return 歌手列表
     */
    public List<ArtistVO> findByPage(int pageNum, int pageSize, String artistName, Integer gender, String area) {
        List<ArtistVO> artists = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, name, gender, avatar, birth, area, introduction FROM tb_artist WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (artistName != null && !artistName.isEmpty()) {
            sql.append("AND name LIKE ? ");
            params.add("%" + artistName + "%");
        }
        if (gender != null) {
            sql.append("AND gender = ? ");
            params.add(gender);
        }
        if (area != null && !area.isEmpty()) {
            sql.append("AND area LIKE ? ");
            params.add("%" + area + "%");
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
                    artists.add(mapResultSetToArtistVO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    /**
     * 统计歌手数量
     */
    public long count(String artistName, Integer gender, String area) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM tb_artist WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (artistName != null && !artistName.isEmpty()) {
            sql.append("AND name LIKE ? ");
            params.add("%" + artistName + "%");
        }
        if (gender != null) {
            sql.append("AND gender = ? ");
            params.add(gender);
        }
        if (area != null && !area.isEmpty()) {
            sql.append("AND area LIKE ? ");
            params.add("%" + area + "%");
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
     * 获取随机歌手
     * @param limit 数量限制
     * @return 歌手列表
     */
    public List<ArtistVO> findRandom(int limit) {
        List<ArtistVO> artists = new ArrayList<>();
        String sql = "SELECT id, name, gender, avatar, birth, area, introduction FROM tb_artist ORDER BY RAND() LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    artists.add(mapResultSetToArtistVO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    /**
     * 获取歌手详情（包含歌曲列表）
     * @param artistId 歌手ID
     * @return 歌手详情
     */
    public ArtistDetailVO findDetailById(Long artistId) {
        String sql = "SELECT id, name, gender, avatar, birth, area, introduction FROM tb_artist WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, artistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ArtistDetailVO artist = new ArtistDetailVO();
                    artist.setArtistId(rs.getLong("id"));
                    artist.setArtistName(rs.getString("name"));
                    artist.setGender(rs.getObject("gender") != null ? rs.getInt("gender") : null);
                    artist.setAvatar(rs.getString("avatar"));
                    Date birth = rs.getDate("birth");
                    if (birth != null) {
                        artist.setBirth(birth.toLocalDate());
                    }
                    artist.setArea(rs.getString("area"));
                    artist.setIntroduction(rs.getString("introduction"));

                    // 获取歌手的歌曲列表
                    artist.setSongs(getSongsByArtistId(artistId));
                    return artist;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取歌手的歌曲列表
     */
    private List<SongDao.SongWithArtist> getSongsByArtistId(Long artistId) {
        List<SongDao.SongWithArtist> songs = new ArrayList<>();
        String sql = "SELECT s.id, s.artist_id, s.name, s.album, s.duration, s.style, s.cover_url, s.audio_url, s.release_time, a.name as artist_name " +
                "FROM tb_song s LEFT JOIN tb_artist a ON s.artist_id = a.id WHERE s.artist_id = ? ORDER BY s.id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, artistId);
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
     * 将ResultSet映射为ArtistVO对象
     */
    private ArtistVO mapResultSetToArtistVO(ResultSet rs) throws SQLException {
        ArtistVO artist = new ArtistVO();
        artist.setArtistId(rs.getLong("id"));
        artist.setArtistName(rs.getString("name"));
        artist.setGender(rs.getObject("gender") != null ? rs.getInt("gender") : null);
        artist.setAvatar(rs.getString("avatar"));
        Date birth = rs.getDate("birth");
        if (birth != null) {
            artist.setBirth(birth.toLocalDate());
        }
        artist.setArea(rs.getString("area"));
        artist.setIntroduction(rs.getString("introduction"));
        return artist;
    }

    /**
     * 歌手VO类
     */
    public static class ArtistVO {
        private Long artistId;
        private String artistName;
        private Integer gender;
        private String avatar;
        private LocalDate birth;
        private String area;
        private String introduction;

        // Getter和Setter
        public Long getArtistId() { return artistId; }
        public void setArtistId(Long artistId) { this.artistId = artistId; }
        public String getArtistName() { return artistName; }
        public void setArtistName(String artistName) { this.artistName = artistName; }
        public Integer getGender() { return gender; }
        public void setGender(Integer gender) { this.gender = gender; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public LocalDate getBirth() { return birth; }
        public void setBirth(LocalDate birth) { this.birth = birth; }
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getIntroduction() { return introduction; }
        public void setIntroduction(String introduction) { this.introduction = introduction; }
    }

    /**
     * 歌手详情VO类（包含歌曲列表）
     */
    public static class ArtistDetailVO extends ArtistVO {
        private List<SongDao.SongWithArtist> songs;

        public List<SongDao.SongWithArtist> getSongs() { return songs; }
        public void setSongs(List<SongDao.SongWithArtist> songs) { this.songs = songs; }
    }
}


