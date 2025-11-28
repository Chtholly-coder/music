package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.FeedbackDao;
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
 * 反馈Servlet
 * 处理用户反馈相关的HTTP请求
 * 路径映射: /feedback/*
 */
@WebServlet("/feedback/*")
public class FeedbackServlet extends HttpServlet {

    private FeedbackDao feedbackDao;

    @Override
    public void init() throws ServletException {
        feedbackDao = new FeedbackDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/addFeedback".equals(pathInfo)) {
            // POST /feedback/addFeedback?content=xxx - 添加反馈
            addFeedback(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 添加反馈
     * POST /feedback/addFeedback?content=xxx
     */
    private void addFeedback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        String content = request.getParameter("content");

        if (content == null || content.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.error("反馈内容不能为空"));
            return;
        }

        int result = feedbackDao.addFeedback(userId, content);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("反馈提交成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("反馈提交失败"));
        }
    }
}


