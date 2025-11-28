package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.BannerDao;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 轮播图Servlet
 * 处理轮播图相关的HTTP请求
 * 路径映射: /banner/*
 */
@WebServlet("/banner/*")
public class BannerServlet extends HttpServlet {

    private BannerDao bannerDao;

    @Override
    public void init() throws ServletException {
        bannerDao = new BannerDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/getBannerList".equals(pathInfo)) {
            // GET /banner/getBannerList - 获取轮播图列表（用户端）
            getBannerList(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 获取轮播图列表（用户端，只返回启用状态的轮播图）
     * GET /banner/getBannerList
     */
    private void getBannerList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<BannerDao.BannerVO> banners = bannerDao.findEnabledBanners();
        JsonUtil.writeJsonToResponse(response, Result.success(banners));
    }
}


