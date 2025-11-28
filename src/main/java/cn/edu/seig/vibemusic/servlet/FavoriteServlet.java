package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.UserFavoriteDao;
import cn.edu.seig.vibemusic.dao.SongDao;
import cn.edu.seig.vibemusic.dao.PlaylistDao;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户收藏Servlet
 * 处理用户收藏相关的HTTP请求
 * 路径映射: /favorite/*
 */
@WebServlet("/favorite/*")
public class FavoriteServlet extends HttpServlet {

    private UserFavoriteDao userFavoriteDao;
    private SongDao songDao;
    private PlaylistDao playlistDao;

    @Override
    public void init() throws ServletException {
        userFavoriteDao = new UserFavoriteDao();
        songDao = new SongDao();
        playlistDao = new PlaylistDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getFavoriteSongs".equals(pathInfo)) {
            // POST /favorite/getFavoriteSongs - 获取用户收藏的歌曲列表
            getFavoriteSongs(request, response);
        } else if ("/collectSong".equals(pathInfo)) {
            // POST /favorite/collectSong?songId=xxx - 收藏歌曲
            collectSong(request, response);
        } else if ("/getFavoritePlaylists".equals(pathInfo)) {
            // POST /favorite/getFavoritePlaylists - 获取用户收藏的歌单列表
            getFavoritePlaylists(request, response);
        } else if ("/collectPlaylist".equals(pathInfo)) {
            // POST /favorite/collectPlaylist?playlistId=xxx - 收藏歌单
            collectPlaylist(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/cancelCollectSong".equals(pathInfo)) {
            // DELETE /favorite/cancelCollectSong?songId=xxx - 取消收藏歌曲
            cancelCollectSong(request, response);
        } else if ("/cancelCollectPlaylist".equals(pathInfo)) {
            // DELETE /favorite/cancelCollectPlaylist?playlistId=xxx - 取消收藏歌单
            cancelCollectPlaylist(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 获取用户收藏的歌曲列表
     * POST /favorite/getFavoriteSongs
     */
    private void getFavoriteSongs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        int pageNum = 1;
        int pageSize = 10;
        if (params != null) {
            if (params.get("pageNum") != null) {
                pageNum = ((Number) params.get("pageNum")).intValue();
            }
            if (params.get("pageSize") != null) {
                pageSize = ((Number) params.get("pageSize")).intValue();
            }
        }

        // 这里简化处理，实际应该查询用户收藏的歌曲
        // 需要在SongDao中添加相应方法
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(0L, null)));
    }

    /**
     * 收藏歌曲
     * POST /favorite/collectSong?songId=xxx
     */
    private void collectSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        String songIdStr = request.getParameter("songId");

        if (songIdStr == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲ID不能为空"));
            return;
        }

        Long songId;
        try {
            songId = Long.parseLong(songIdStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲ID格式错误"));
            return;
        }

        // 检查是否已收藏
        if (userFavoriteDao.isSongFavorite(userId, songId)) {
            JsonUtil.writeJsonToResponse(response, Result.error("已收藏该歌曲"));
            return;
        }

        int result = userFavoriteDao.collectSong(userId, songId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("收藏成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("收藏失败"));
        }
    }

    /**
     * 取消收藏歌曲
     * DELETE /favorite/cancelCollectSong?songId=xxx
     */
    private void cancelCollectSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        String songIdStr = request.getParameter("songId");

        if (songIdStr == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲ID不能为空"));
            return;
        }

        Long songId;
        try {
            songId = Long.parseLong(songIdStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲ID格式错误"));
            return;
        }

        int result = userFavoriteDao.cancelCollectSong(userId, songId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("取消收藏成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("取消收藏失败"));
        }
    }

    /**
     * 获取用户收藏的歌单列表
     * POST /favorite/getFavoritePlaylists
     */
    private void getFavoritePlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        // 简化处理
        JsonUtil.writeJsonToResponse(response, Result.success(new PageResult<>(0L, null)));
    }

    /**
     * 收藏歌单
     * POST /favorite/collectPlaylist?playlistId=xxx
     */
    private void collectPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        String playlistIdStr = request.getParameter("playlistId");

        if (playlistIdStr == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单ID不能为空"));
            return;
        }

        Long playlistId;
        try {
            playlistId = Long.parseLong(playlistIdStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单ID格式错误"));
            return;
        }

        if (userFavoriteDao.isPlaylistFavorite(userId, playlistId)) {
            JsonUtil.writeJsonToResponse(response, Result.error("已收藏该歌单"));
            return;
        }

        int result = userFavoriteDao.collectPlaylist(userId, playlistId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("收藏成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("收藏失败"));
        }
    }

    /**
     * 取消收藏歌单
     * DELETE /favorite/cancelCollectPlaylist?playlistId=xxx
     */
    private void cancelCollectPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        String playlistIdStr = request.getParameter("playlistId");

        if (playlistIdStr == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单ID不能为空"));
            return;
        }

        Long playlistId;
        try {
            playlistId = Long.parseLong(playlistIdStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌单ID格式错误"));
            return;
        }

        int result = userFavoriteDao.cancelCollectPlaylist(userId, playlistId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("取消收藏成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("取消收藏失败"));
        }
    }
}


