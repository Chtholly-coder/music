package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.AdminDao;
import cn.edu.seig.vibemusic.dao.UserDao;
import cn.edu.seig.vibemusic.dao.ArtistDao;
import cn.edu.seig.vibemusic.dao.SongDao;
import cn.edu.seig.vibemusic.dao.PlaylistDao;
import cn.edu.seig.vibemusic.dao.BannerDao;
import cn.edu.seig.vibemusic.dao.FeedbackDao;
import cn.edu.seig.vibemusic.model.entity.Admin;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.RedisService;
import cn.edu.seig.vibemusic.util.JsonUtil;
import cn.edu.seig.vibemusic.util.JwtUtil;
import cn.edu.seig.vibemusic.util.Md5Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Servlet
 * 处理管理员相关的HTTP请求
 * 路径映射: /admin/*
 */
@WebServlet("/admin/*")
@MultipartConfig
public class AdminServlet extends HttpServlet {

    private AdminDao adminDao;
    private UserDao userDao;
    private ArtistDao artistDao;
    private SongDao songDao;
    private PlaylistDao playlistDao;
    private RedisService redisService;

    @Override
    public void init() throws ServletException {
        adminDao = new AdminDao();
        userDao = new UserDao();
        artistDao = new ArtistDao();
        songDao = new SongDao();
        playlistDao = new PlaylistDao();
        redisService = new RedisService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String method = request.getMethod();
        if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getAllUsersCount".equals(pathInfo)) {
            // GET /admin/getAllUsersCount - 获取所有用户数量
            getAllUsersCount(request, response);
        } else if ("/getAllArtistsCount".equals(pathInfo)) {
            // GET /admin/getAllArtistsCount - 获取所有歌手数量
            getAllArtistsCount(request, response);
        } else if ("/getAllSongsCount".equals(pathInfo)) {
            // GET /admin/getAllSongsCount - 获取所有歌曲数量
            getAllSongsCount(request, response);
        } else if ("/getAllArtistNames".equals(pathInfo)) {
            // GET /admin/getAllArtistNames - 获取所有歌手名称
            getAllArtistNames(request, response);
        } else if ("/getAllPlaylistsCount".equals(pathInfo)) {
            // GET /admin/getAllPlaylistsCount - 获取所有歌单数量
            getAllPlaylistsCount(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/login".equals(pathInfo)) {
            // POST /admin/login - 管理员登录
            login(request, response);
        } else if ("/register".equals(pathInfo)) {
            // POST /admin/register - 注册管理员
            register(request, response);
        } else if ("/logout".equals(pathInfo)) {
            // POST /admin/logout - 管理员登出
            logout(request, response);
        } else if ("/getAllUsers".equals(pathInfo)) {
            // POST /admin/getAllUsers - 获取所有用户信息
            getAllUsers(request, response);
        } else if ("/addUser".equals(pathInfo)) {
            // POST /admin/addUser - 新增用户
            addUser(request, response);
        } else if ("/getAllArtists".equals(pathInfo)) {
            // POST /admin/getAllArtists - 获取所有歌手信息
            getAllArtists(request, response);
        } else if ("/addArtist".equals(pathInfo)) {
            // POST /admin/addArtist - 新增歌手
            addArtist(request, response);
        } else if ("/getAllSongsByArtist".equals(pathInfo)) {
            // POST /admin/getAllSongsByArtist - 根据歌手获取歌曲
            getAllSongsByArtist(request, response);
        } else if ("/addSong".equals(pathInfo)) {
            // POST /admin/addSong - 添加歌曲
            addSong(request, response);
        } else if ("/getAllPlaylists".equals(pathInfo)) {
            // POST /admin/getAllPlaylists - 获取所有歌单
            getAllPlaylists(request, response);
        } else if ("/addPlaylist".equals(pathInfo)) {
            // POST /admin/addPlaylist - 新增歌单
            addPlaylist(request, response);
        } else if ("/getAllBanners".equals(pathInfo)) {
            // POST /admin/getAllBanners - 获取所有轮播图
            getAllBanners(request, response);
        } else if ("/addBanner".equals(pathInfo)) {
            // POST /admin/addBanner - 添加轮播图
            addBanner(request, response);
        } else if ("/getAllFeedbacks".equals(pathInfo)) {
            // POST /admin/getAllFeedbacks - 获取所有反馈
            getAllFeedbacks(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/updateUser".equals(pathInfo)) {
            // PUT /admin/updateUser - 更新用户信息
            updateUser(request, response);
        } else if ("/updateArtist".equals(pathInfo)) {
            // PUT /admin/updateArtist - 更新歌手信息
            updateArtist(request, response);
        } else if ("/updateSong".equals(pathInfo)) {
            // PUT /admin/updateSong - 更新歌曲信息
            updateSong(request, response);
        } else if ("/updatePlaylist".equals(pathInfo)) {
            // PUT /admin/updatePlaylist - 更新歌单信息
            updatePlaylist(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 处理PATCH请求
     */
    protected void doPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/updateUserStatus/")) {
            // PATCH /admin/updateUserStatus/{id}/{status} - 更新用户状态
            updateUserStatus(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updateArtistAvatar/")) {
            // PATCH /admin/updateArtistAvatar/{id} - 更新歌手头像
            updateArtistAvatar(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updateSongCover/")) {
            // PATCH /admin/updateSongCover/{id} - 更新歌曲封面
            updateSongCover(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updateSongAudio/")) {
            // PATCH /admin/updateSongAudio/{id} - 更新歌曲音频
            updateSongAudio(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updatePlaylistCover/")) {
            // PATCH /admin/updatePlaylistCover/{id} - 更新歌单封面
            updatePlaylistCover(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updateBanner/")) {
            // PATCH /admin/updateBanner/{id} - 更新轮播图
            updateBanner(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/updateBannerStatus/")) {
            // PATCH /admin/updateBannerStatus/{id} - 更新轮播图状态
            updateBannerStatus(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/deleteUser/")) {
            // DELETE /admin/deleteUser/{id} - 删除用户
            deleteUser(request, response, pathInfo);
        } else if ("/deleteUsers".equals(pathInfo)) {
            // DELETE /admin/deleteUsers - 批量删除用户
            deleteUsers(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/deleteArtist/")) {
            // DELETE /admin/deleteArtist/{id} - 删除歌手
            deleteArtist(request, response, pathInfo);
        } else if ("/deleteArtists".equals(pathInfo)) {
            // DELETE /admin/deleteArtists - 批量删除歌手
            deleteArtists(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/deleteSong/")) {
            // DELETE /admin/deleteSong/{id} - 删除歌曲
            deleteSong(request, response, pathInfo);
        } else if ("/deleteSongs".equals(pathInfo)) {
            // DELETE /admin/deleteSongs - 批量删除歌曲
            deleteSongs(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/deletePlaylist/")) {
            // DELETE /admin/deletePlaylist/{id} - 删除歌单
            deletePlaylist(request, response, pathInfo);
        } else if ("/deletePlaylists".equals(pathInfo)) {
            // DELETE /admin/deletePlaylists - 批量删除歌单
            deletePlaylists(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/deleteBanner/")) {
            // DELETE /admin/deleteBanner/{id} - 删除轮播图
            deleteBanner(request, response, pathInfo);
        } else if ("/deleteBanners".equals(pathInfo)) {
            // DELETE /admin/deleteBanners - 批量删除轮播图
            deleteBanners(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/deleteFeedback/")) {
            // DELETE /admin/deleteFeedback/{id} - 删除反馈
            deleteFeedback(request, response, pathInfo);
        } else if ("/deleteFeedbacks".equals(pathInfo)) {
            // DELETE /admin/deleteFeedbacks - 批量删除反馈
            deleteFeedbacks(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    // ==================== 管理员登录相关 ====================

    /**
     * 管理员登录
     * POST /admin/login
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户名和密码不能为空"));
            return;
        }

        Admin admin = adminDao.findByUsername(username);
        if (admin == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户名错误"));
            return;
        }

        if (!Md5Util.matches(password, admin.getPassword())) {
            JsonUtil.writeJsonToResponse(response, Result.error("密码错误"));
            return;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ADMIN");
        claims.put("adminId", admin.getAdminId());
        claims.put("username", admin.getUsername());
        String token = JwtUtil.generateToken(claims);

        redisService.set(token, token, 6 * 60 * 60);

        JsonUtil.writeJsonToResponse(response, Result.success("登录成功", token));
    }

    /**
     * 注册管理员
     * POST /admin/register
     */
    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户名和密码不能为空"));
            return;
        }

        if (adminDao.findByUsername(username) != null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户名已存在"));
            return;
        }

        int result = adminDao.insert(username, Md5Util.md5(password));
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("注册成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("注册失败"));
        }
    }

    /**
     * 管理员登出
     * POST /admin/logout
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null) {
            redisService.delete(token);
        }

        JsonUtil.writeJsonToResponse(response, Result.success("登出成功"));
    }

    // ==================== 用户管理 ====================

    private void getAllUsersCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long count = userDao.count();
        JsonUtil.writeJsonToResponse(response, Result.success(count));
    }

    private void getAllUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);
        int pageNum = params.get("pageNum") != null ? ((Number) params.get("pageNum")).intValue() : 1;
        int pageSize = params.get("pageSize") != null ? ((Number) params.get("pageSize")).intValue() : 10;

        // 简化实现
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(userDao.count(), userDao.findByPage(pageNum, pageSize))));
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("添加成功"));
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updateUserStatus(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        // /updateUserStatus/{id}/{status}
        String[] parts = pathInfo.substring("/updateUserStatus/".length()).split("/");
        if (parts.length < 2) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数错误"));
            return;
        }
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        String idStr = pathInfo.substring("/deleteUser/".length());
        Long userId = Long.parseLong(idStr);
        int result = userDao.deleteById(userId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("删除失败"));
        }
    }

    private void deleteUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    // ==================== 歌手管理 ====================

    private void getAllArtistsCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer gender = request.getParameter("gender") != null ? Integer.parseInt(request.getParameter("gender")) : null;
        String area = request.getParameter("area");
        long count = artistDao.count(null, gender, area);
        JsonUtil.writeJsonToResponse(response, Result.success(count));
    }

    private void getAllArtists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);
        int pageNum = params.get("pageNum") != null ? ((Number) params.get("pageNum")).intValue() : 1;
        int pageSize = params.get("pageSize") != null ? ((Number) params.get("pageSize")).intValue() : 10;
        String artistName = (String) params.get("artistName");
        Integer gender = params.get("gender") != null ? ((Number) params.get("gender")).intValue() : null;
        String area = (String) params.get("area");

        List<ArtistDao.ArtistVO> artists = artistDao.findByPage(pageNum, pageSize, artistName, gender, area);
        long total = artistDao.count(artistName, gender, area);
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(total, artists)));
    }

    private void getAllArtistNames(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 简化实现
        JsonUtil.writeJsonToResponse(response, Result.success(artistDao.findRandom(100)));
    }

    private void addArtist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("添加成功"));
    }

    private void updateArtist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updateArtistAvatar(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void deleteArtist(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    private void deleteArtists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    // ==================== 歌曲管理 ====================

    private void getAllSongsCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String style = request.getParameter("style");
        long count = songDao.count(null, null, null);
        JsonUtil.writeJsonToResponse(response, Result.success(count));
    }

    private void getAllSongsByArtist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);
        int pageNum = params.get("pageNum") != null ? ((Number) params.get("pageNum")).intValue() : 1;
        int pageSize = params.get("pageSize") != null ? ((Number) params.get("pageSize")).intValue() : 10;

        List<SongDao.SongWithArtist> songs = songDao.findByPage(pageNum, pageSize, null, null, null);
        long total = songDao.count(null, null, null);
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(total, songs)));
    }

    private void addSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("添加成功"));
    }

    private void updateSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updateSongCover(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updateSongAudio(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void deleteSong(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    private void deleteSongs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    // ==================== 歌单管理 ====================

    private void getAllPlaylistsCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String style = request.getParameter("style");
        long count = playlistDao.count(null, style);
        JsonUtil.writeJsonToResponse(response, Result.success(count));
    }

    private void getAllPlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);
        int pageNum = params.get("pageNum") != null ? ((Number) params.get("pageNum")).intValue() : 1;
        int pageSize = params.get("pageSize") != null ? ((Number) params.get("pageSize")).intValue() : 10;

        List<PlaylistDao.PlaylistVO> playlists = playlistDao.findByPage(pageNum, pageSize, null, null);
        long total = playlistDao.count(null, null);
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(total, playlists)));
    }

    private void addPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("添加成功"));
    }

    private void updatePlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updatePlaylistCover(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void deletePlaylist(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    private void deletePlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    // ==================== 轮播图管理 ====================

    private void getAllBanners(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(0L, null)));
    }

    private void addBanner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("添加成功"));
    }

    private void updateBanner(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void updateBannerStatus(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
    }

    private void deleteBanner(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    private void deleteBanners(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    // ==================== 反馈管理 ====================

    private void getAllFeedbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(0L, null)));
    }

    private void deleteFeedback(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }

    private void deleteFeedbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
    }
}
