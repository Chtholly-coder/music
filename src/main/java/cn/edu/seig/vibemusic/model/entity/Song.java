package cn.edu.seig.vibemusic.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 歌曲实体类
 */
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌曲ID
     */
    private Long songId;

    /**
     * 歌手ID
     */
    private Long artistId;

    /**
     * 歌曲名称
     */
    private String songName;

    /**
     * 专辑名称
     */
    private String album;

    /**
     * 歌词
     */
    private String lyric;

    /**
     * 歌曲时长
     */
    private String duration;

    /**
     * 歌曲风格
     */
    private String style;

    /**
     * 歌曲封面URL
     */
    private String coverUrl;

    /**
     * 歌曲音频URL
     */
    private String audioUrl;

    /**
     * 发行日期
     */
    private LocalDate releaseTime;

    // Getter和Setter方法
    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public LocalDate getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDate releaseTime) {
        this.releaseTime = releaseTime;
    }
}


