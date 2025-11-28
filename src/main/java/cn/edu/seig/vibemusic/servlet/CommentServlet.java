package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.CommentDao;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 评论Servlet
 * 处理评论相关的HTTP请求
 * 路径映射: /comment/*
 */
@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {

    private CommentDao commentDao;

    @Override
    public void init() throws ServletException {
        commentDao = new CommentDao();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String method = request.getMethod();
        // 处理PATCH请求
        if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/addSongComment".equals(pathInfo)) {
            // POST /comment/addSongComment - 新增歌曲评论
            addSongComment(request, response);
        } else if ("/addPlaylistComment".equals(pathInfo)) {
            // POST /comment/addPlaylistComment - 新增歌单评论
            addPlaylistComment(request, response);
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

        if (pathInfo != null && pathInfo.startsWith("/likeComment/")) {
            // PATCH /comment/likeComment/{id} - 点赞评论
            likeComment(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/cancelLikeComment/")) {
            // PATCH /comment/cancelLikeComment/{id} - 取消点赞评论
            cancelLikeComment(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/deleteComment/")) {
            // DELETE /comment/deleteComment/{id} - 删除评论
            deleteComment(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 新增歌曲评论
     * POST /comment/addSongComment
     * 请求体: { songId, content }
     */
    private void addSongComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        Long songId = params.get("songId") != null ? ((Number) params.get("songId")).longValue() : null;
        String content = (String) params.get("content");

        if (songId == null || content == null || content.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数不完整"));
            return;
        }

        int result = commentDao.addSongComment(userId, songId, content);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("评论成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("评论失败"));
        }
    }

    /**
     * 新增歌单评论
     * POST /comment/addPlaylistComment
     * 请求体: { playlistId, content }
     */
    private void addPlaylistComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        Map<String, Object> params = JsonUtil.readJsonFromRequest(request, Map.class);

        Long playlistId = params.get("playlistId") != null ? ((Number) params.get("playlistId")).longValue() : null;
        String content = (String) params.get("content");

        if (playlistId == null || content == null || content.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数不完整"));
            return;
        }

        int result = commentDao.addPlaylistComment(userId, playlistId, content);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("评论成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("评论失败"));
        }
    }

    /**
     * 点赞评论
     * PATCH /comment/likeComment/{id}
     */
    private void likeComment(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        String idStr = pathInfo.substring("/likeComment/".length());
        Long commentId;
        try {
            commentId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("评论ID格式错误"));
            return;
        }

        int result = commentDao.likeComment(commentId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("点赞成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("点赞失败"));
        }
    }

    /**
     * 取消点赞评论
     * PATCH /comment/cancelLikeComment/{id}
     */
    private void cancelLikeComment(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        String idStr = pathInfo.substring("/cancelLikeComment/".length());
        Long commentId;
        try {
            commentId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("评论ID格式错误"));
            return;
        }

        int result = commentDao.cancelLikeComment(commentId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("取消点赞成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("取消点赞失败"));
        }
    }

    /**
     * 删除评论
     * DELETE /comment/deleteComment/{id}
     */
    private void deleteComment(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        String idStr = pathInfo.substring("/deleteComment/".length());
        Long commentId;
        try {
            commentId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonToResponse(response, Result.error("评论ID格式错误"));
            return;
        }

        int result = commentDao.deleteComment(commentId);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("删除失败"));
        }
    }
}


