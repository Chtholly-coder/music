package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.ArtistDao;
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
 * 歌手Servlet
 * 处理歌手相关的HTTP请求
 * 路径映射: /artist/*
 */
@WebServlet("/artist/*")
public class ArtistServlet extends HttpServlet {

    private ArtistDao artistDao;

    @Override
    public void init() throws ServletException {
        artistDao = new ArtistDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getRandomArtists".equals(pathInfo)) {
            // GET /artist/getRandomArtists - 获取随机歌手
            getRandomArtists(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/getArtistDetail/")) {
            // GET /artist/getArtistDetail/{id} - 获取歌手详情
            getArtistDetail(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getAllArtists".equals(pathInfo)) {
            // POST /artist/getAllArtists - 获取所有歌手（分页）
            getAllArtists(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 获取所有歌手（分页查询）
     * POST /artist/getAllArtists
     * 请求体: { pageNum, pageSize, artistName, gender, area }
     */
    private void getAllArtists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        int pageNum = 1;
        int pageSize = 10;
        String artistName = null;
        Integer gender = null;
        String area = null;

        if (params != null) {
            if (params.get("pageNum") != null) {
                pageNum = ((Number) params.get("pageNum")).intValue();
            }
            if (params.get("pageSize") != null) {
                pageSize = ((Number) params.get("pageSize")).intValue();
            }
            artistName = (String) params.get("artistName");
            if (params.get("gender") != null) {
                gender = ((Number) params.get("gender")).intValue();
            }
            area = (String) params.get("area");
        }

        List<ArtistDao.ArtistVO> artists = artistDao.findByPage(pageNum, pageSize, artistName, gender, area);
        long total = artistDao.count(artistName, gender, area);

        if (artists.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.success("未找到相关数据", new PageResult<>(0L, null)));
            return;
        }

        PageResult<ArtistDao.ArtistVO> pageResult = new PageResult<>(total, artists);
        JsonUtil.writeJsonToResponse(response, Result.success(pageResult));
    }

    /**
     * 获取随机歌手
     * GET /artist/getRandomArtists
     */
    private void getRandomArtists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ArtistDao.ArtistVO> artists = artistDao.findRandom(10);
        JsonUtil.writeJsonToResponse(response, Result.success(artists));
    }

    /**
     * 获取歌手详情
     * GET /artist/getArtistDetail/{id}
     */
    private void getArtistDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        String idStr = pathInfo.substring("/getArtistDetail/".length());
        Long artistId;
        try {
            artistId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌手ID格式错误"));
            return;
        }

        ArtistDao.ArtistDetailVO artist = artistDao.findDetailById(artistId);
        if (artist == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("歌手不存在"));
            return;
        }

        JsonUtil.writeJsonToResponse(response, Result.success(artist));
    }
}


