package cn.edu.seig.vibemusic.dao;

import cn.edu.seig.vibemusic.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图数据访问对象
 * 负责轮播图相关的数据库操作
 */
public class BannerDao {

    /**
     * 获取启用状态的轮播图列表
     * @return 轮播图列表
     */
    public List<BannerVO> findEnabledBanners() {
        List<BannerVO> banners = new ArrayList<>();
        String sql = "SELECT id, banner_url, status, create_time FROM tb_banner WHERE status = 0 ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                banners.add(mapResultSetToBannerVO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }

    /**
     * 将ResultSet映射为BannerVO对象
     */
    private BannerVO mapResultSetToBannerVO(ResultSet rs) throws SQLException {
        BannerVO banner = new BannerVO();
        banner.setBannerId(rs.getLong("id"));
        banner.setBannerUrl(rs.getString("banner_url"));
        banner.setStatus(rs.getInt("status"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            banner.setCreateTime(createTime.toLocalDateTime());
        }
        return banner;
    }

    /**
     * 轮播图VO类
     */
    public static class BannerVO {
        private Long bannerId;
        private String bannerUrl;
        private Integer status;
        private LocalDateTime createTime;

        // Getter和Setter
        public Long getBannerId() { return bannerId; }
        public void setBannerId(Long bannerId) { this.bannerId = bannerId; }
        public String getBannerUrl() { return bannerUrl; }
        public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }
}


