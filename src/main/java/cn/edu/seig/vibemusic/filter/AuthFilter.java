package cn.edu.seig.vibemusic.filter;

import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.RedisService;
import cn.edu.seig.vibemusic.util.JsonUtil;
import cn.edu.seig.vibemusic.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 认证过滤器
 * 拦截需要登录才能访问的接口
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private RedisService redisService;

    // 不需要认证的路径（白名单）
    private static final List<String> WHITE_LIST = Arrays.asList(
            // 用户模块
            "/user/login",
            "/user/register",
            "/user/sendVerificationCode",
            "/user/resetUserPassword",
            // 管理员模块
            "/admin/login",
            // 歌曲模块（公开接口）
            "/song/getAllSongs",
            "/song/getRecommendedSongs",
            "/song/getSongDetail",
            // 歌手模块（公开接口）
            "/artist/getAllArtists",
            "/artist/getRandomArtists",
            "/artist/getArtistDetail",
            // 歌单模块（公开接口）
            "/playlist/getAllPlaylists",
            "/playlist/getRecommendedPlaylists",
            "/playlist/getPlaylistDetail",
            // 轮播图模块（公开接口）
            "/banner/getBannerList",
            // 静态资源
            "/index.html",
            "/"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        redisService = new RedisService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // 检查是否在白名单中
        if (isWhiteListed(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 获取Authorization请求头
        String token = httpRequest.getHeader("Authorization");

        // 处理Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证令牌
        if (token == null || token.isEmpty()) {
            sendUnauthorizedResponse(httpResponse, "未登录，请先登录");
            return;
        }

        // 检查令牌是否在Redis中存在（用于登出验证）
        String storedToken = redisService.get(token);
        if (storedToken == null) {
            sendUnauthorizedResponse(httpResponse, "会话过期，请重新登录");
            return;
        }

        // 解析令牌
        Map<String, Object> claims = JwtUtil.parseToken(token);
        if (claims == null) {
            sendUnauthorizedResponse(httpResponse, "令牌无效");
            return;
        }

        // 将用户信息存入请求属性，供后续使用
        httpRequest.setAttribute("claims", claims);
        httpRequest.setAttribute("token", token);

        // 继续处理请求
        chain.doFilter(request, response);
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListed(String path) {
        for (String whitePath : WHITE_LIST) {
            if (path.startsWith(whitePath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JsonUtil.writeJsonToResponse(response, Result.error(message));
    }

    @Override
    public void destroy() {
        // 销毁时无需特殊处理
    }
}

