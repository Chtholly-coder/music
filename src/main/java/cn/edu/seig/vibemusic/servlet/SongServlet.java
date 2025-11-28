package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.SongDao;
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
import java.util.stream.Collectors;

/**
 * 歌曲Servlet
 * 处理歌曲相关的HTTP请求
 * 路径映射: /song/*
 */
@WebServlet("/song/*")
public class SongServlet extends HttpServlet {

    private SongDao songDao;
    private UserFavoriteDao userFavoriteDao;

    @Override
    public void init() throws ServletException {
        songDao = new SongDao();
        userFavoriteDao = new UserFavoriteDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getRecommendedSongs".equals(pathInfo)) {
            // GET /song/getRecommendedSongs - 获取推荐歌曲
            getRecommendedSongs(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/getSongDetail/")) {
            // GET /song/getSongDetail/{id} - 获取歌曲详情
            getSongDetail(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getAllSongs".equals(pathInfo)) {
            // POST /song/getAllSongs - 获取所有歌曲（分页）
            getAllSongs(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 获取所有歌曲（分页查询）
     * POST /song/getAllSongs
     * 请求体: { pageNum, pageSize, songName, artistName, album }
     */
    private void getAllSongs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        // 获取分页参数
        int pageNum = 1;
        int pageSize = 10;
        String songName = null;
        String artistName = null;
        String album = null;

        if (params != null) {
            if (params.get("pageNum") != null) {
                pageNum = ((Number) params.get("pageNum")).intValue();
            }
            if (params.get("pageSize") != null) {
                pageSize = ((Number) params.get("pageSize")).intValue();
            }
            songName = (String) params.get("songName");
            artistName = (String) params.get("artistName");
            album = (String) params.get("album");
        }

        // 查询歌曲列表
        List<SongDao.SongWithArtist> songs = songDao.findByPage(pageNum, pageSize, songName, artistName, album);
        long total = songDao.count(songName, artistName, album);

        // 获取用户收藏状态
        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            Set<Long> favoriteSongIds = userFavoriteDao.getFavoriteSongIds(userId);
            for (SongDao.SongWithArtist song : songs) {
                if (favoriteSongIds.contains(song.getSongId())) {
                    song.setLikeStatus(1); // 1-已收藏
                }
            }
        }

        if (songs.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.success("未找到相关数据", new PageResult<>(0L, null)));
            return;
        }

        PageResult<SongDao.SongWithArtist> pageResult = new PageResult<>(total, songs);
        JsonUtil.writeJsonToResponse(response, Result.success(pageResult));
    }

    /**
     * 获取推荐歌曲
     * GET /song/getRecommendedSongs
     */
    private void getRecommendedSongs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取20首随机歌曲作为推荐
        List<SongDao.SongWithArtist> songs = songDao.findRandom(20);

        // 获取用户收藏状态
        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            Set<Long> favoriteSongIds = userFavoriteDao.getFavoriteSongIds(userId);
            for (SongDao.SongWithArtist song : songs) {
                if (favoriteSongIds.contains(song.getSongId())) {
                    song.setLikeStatus(1);
                }
            }
        }

        JsonUtil.writeJsonToResponse(response, Result.success(songs));
    }

    /**
     * 获取歌曲详情
     * GET /song/getSongDetail/{id}
     */
    private void getSongDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        // 从路径中提取歌曲ID
        String idStr = pathInfo.substring("/getSongDetail/".length());
        Long songId;
        try {
            songId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲ID格式错误"));
            return;
        }

        // 查询歌曲详情
        SongDao.SongWithArtist song = songDao.findDetailById(songId);
        if (song == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌曲不存在"));
            return;
        }

        // 获取用户收藏状态
        Long userId = getUserIdFromToken(request);
        if (userId != null) {
            boolean isFavorite = userFavoriteDao.isSongFavorite(userId, songId);
            if (isFavorite) {
                song.setLikeStatus(1);
            }
        }

        JsonUtil.writeJsonToResponse(response, Result.success(song));
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
