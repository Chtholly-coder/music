package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.PlaylistDao;
import cn.edu.seig.vibemusic.dao.UserFavoriteDao;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.util.JsonUtil;
import cn.edu.seig.vibemusic.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 歌单Servlet
 * 处理歌单相关的HTTP请求
 * 路径映射: /playlist/*
 */
@WebServlet("/playlist/*")
public class PlaylistServlet extends HttpServlet {

    private PlaylistDao playlistDao;
    private UserFavoriteDao userFavoriteDao;

    @Override
    public void init() throws ServletException {
        playlistDao = new PlaylistDao();
        userFavoriteDao = new UserFavoriteDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getRecommendedPlaylists".equals(pathInfo)) {
            // GET /playlist/getRecommendedPlaylists - 获取推荐歌单
            getRecommendedPlaylists(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/getPlaylistDetail/")) {
            // GET /playlist/getPlaylistDetail/{id} - 获取歌单详情
            getPlaylistDetail(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getAllPlaylists".equals(pathInfo)) {
            // POST /playlist/getAllPlaylists - 获取所有歌单（分页）
            getAllPlaylists(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 获取所有歌单（分页查询）
     * POST /playlist/getAllPlaylists
     */
    private void getAllPlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        int pageNum = 1;
        int pageSize = 10;
        String playlistName = null;
        String style = null;

        if (params != null) {
            if (params.get("pageNum") != null) {
                pageNum = ((Number) params.get("pageNum")).intValue();
            }
            if (params.get("pageSize") != null) {
                pageSize = ((Number) params.get("pageSize")).intValue();
            }
            playlistName = (String) params.get("playlistName");
            style = (String) params.get("style");
        }

        List<PlaylistDao.PlaylistVO> playlists = playlistDao.findByPage(pageNum, pageSize, playlistName, style);
        long total = playlistDao.count(playlistName, style);

        // 获取用户收藏状态
        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            Set<Long> favoritePlaylistIds = userFavoriteDao.getFavoritePlaylistIds(userId);
            for (PlaylistDao.PlaylistVO playlist : playlists) {
                if (favoritePlaylistIds.contains(playlist.getPlaylistId())) {
                    playlist.setLikeStatus(1);
                }
            }
        }

        if (playlists.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.success("未找到相关数据", new PageResult<>(0L, null)));
            return;
        }

        PageResult<PlaylistDao.PlaylistVO> pageResult = new PageResult<>(total, playlists);
        JsonUtil.writeJsonToResponse(response, Result.success(pageResult));
    }

    /**
     * 获取推荐歌单
     * GET /playlist/getRecommendedPlaylists
     */
    private void getRecommendedPlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<PlaylistDao.PlaylistVO> playlists = playlistDao.findRandom(10);

        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            Set<Long> favoritePlaylistIds = userFavoriteDao.getFavoritePlaylistIds(userId);
            for (PlaylistDao.PlaylistVO playlist : playlists) {
                if (favoritePlaylistIds.contains(playlist.getPlaylistId())) {
                    playlist.setLikeStatus(1);
                }
            }
        }

        JsonUtil.writeJsonToResponse(response, Result.success(playlists));
    }

    /**
     * 获取歌单详情
     * GET /playlist/getPlaylistDetail/{id}
     */
    private void getPlaylistDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        String idStr = pathInfo.substring("/getPlaylistDetail/".length());
        Long playlistId;
        try {
            playlistId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单ID格式错误"));
            return;
        }

        PlaylistDao.PlaylistDetailVO playlist = playlistDao.findDetailById(playlistId);
        if (playlist == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单不存在"));
            return;
        }

        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            boolean isFavorite = userFavoriteDao.isPlaylistFavorite(userId, playlistId);
            if (isFavorite) {
                playlist.setLikeStatus(1);
            }
        }

        JsonUtil.writeJsonToResponse(response, Result.success(playlist));
    }

    /**
     * 从请求头中解析用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && !token.isEmpty()) {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims != null && claims.get("userId") != null) {
                String role = (String) claims.get("role");
                if ("ROLE_USER".equals(role)) {
                    return ((Number) claims.get("userId")).longValue();
                }
            }
        }
        return null;
    }
}


