package cn.edu.seig.vibemusic.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 歌手实体类
 */
public class Artist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌手ID
     */
    private Long artistId;

    /**
     * 歌手姓名
     */
    private String artistName;

    /**
     * 歌手性别：0-男，1-女，2-组合/乐队
     */
    private Integer gender;

    /**
     * 歌手头像URL
     */
    private String avatar;

    /**
     * 出生日期
     */
    private LocalDate birth;

    /**
     * 国籍/地区
     */
    private String area;

    /**
     * 歌手简介
     */
    private String introduction;

    // Getter和Setter方法
    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}


