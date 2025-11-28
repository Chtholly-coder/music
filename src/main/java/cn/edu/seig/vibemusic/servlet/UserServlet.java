package cn.edu.seig.vibemusic.servlet;

import cn.edu.seig.vibemusic.dao.UserDao;
import cn.edu.seig.vibemusic.model.entity.User;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.EmailService;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户Servlet
 * 处理用户相关的HTTP请求
 * 路径映射: /user/*
 */
@WebServlet("/user/*")
@MultipartConfig
public class UserServlet extends HttpServlet {

    private UserDao userDao;
    private RedisService redisService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        redisService = new RedisService();
        emailService = new EmailService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String method = request.getMethod();
        String pathInfo = request.getPathInfo();

        // 处理PATCH请求（Servlet默认不支持PATCH）
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

        if ("/sendVerificationCode".equals(pathInfo)) {
            // GET /user/sendVerificationCode?email=xxx - 发送验证码
            sendVerificationCode(request, response);
        } else if ("/getUserInfo".equals(pathInfo)) {
            // GET /user/getUserInfo - 获取用户信息
            getUserInfo(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/register".equals(pathInfo)) {
            // POST /user/register - 用户注册
            register(request, response);
        } else if ("/login".equals(pathInfo)) {
            // POST /user/login - 用户登录
            login(request, response);
        } else if ("/logout".equals(pathInfo)) {
            // POST /user/logout - 用户登出
            logout(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/updateUserInfo".equals(pathInfo)) {
            // PUT /user/updateUserInfo - 更新用户信息
            updateUserInfo(request, response);
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

        if ("/updateUserAvatar".equals(pathInfo)) {
            // PATCH /user/updateUserAvatar - 更新用户头像
            updateUserAvatar(request, response);
        } else if ("/updateUserPassword".equals(pathInfo)) {
            // PATCH /user/updateUserPassword - 更新用户密码
            updateUserPassword(request, response);
        } else if ("/resetUserPassword".equals(pathInfo)) {
            // PATCH /user/resetUserPassword - 重置用户密码
            resetUserPassword(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/deleteAccount".equals(pathInfo)) {
            // DELETE /user/deleteAccount - 注销账号
            deleteAccount(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtil.writeJsonToResponse(response, Result.error("接口不存在"));
        }
    }

    /**
     * 发送验证码
     * GET /user/sendVerificationCode?email=xxx
     */
    private void sendVerificationCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");

        if (email == null || email.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮箱不能为空"));
            return;
        }

        // 发送验证码邮件
        String code = emailService.sendVerificationCode(email);
        if (code == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮件发送失败"));
            return;
        }

        // 将验证码存入Redis，有效期5分钟
        redisService.set("verificationCode:" + email, code, 300);

        JsonUtil.writeJsonToResponse(response, Result.success("邮件发送成功"));
    }

    /**
     * 用户注册
     * POST /user/register
     * 请求体: { username, password, email, verificationCode }
     */
    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        String verificationCode = params.get("verificationCode");

        // 参数校验
        if (username == null || password == null || email == null || verificationCode == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数不完整"));
            return;
        }

        // 验证验证码
        String storedCode = redisService.get("verificationCode:" + email);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            JsonUtil.writeJsonToResponse(response, Result.error("验证码无效"));
            return;
        }

        // 检查用户名是否已存在
        if (userDao.findByUsername(username) != null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户名已存在"));
            return;
        }

        // 检查邮箱是否已存在
        if (userDao.findByEmail(email) != null) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮箱已存在"));
            return;
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(Md5Util.md5(password));
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setUserStatus(0); // 0-启用

        int result = userDao.insert(user);
        if (result > 0) {
            redisService.delete("verificationCode:" + email);
            JsonUtil.writeJsonToResponse(response, Result.success("注册成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("注册失败"));
        }
    }

    /**
     * 用户登录
     * POST /user/login
     * 请求体: { email, password }
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String email = params.get("email");
        String password = params.get("password");

        if (email == null || password == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮箱和密码不能为空"));
            return;
        }

        User user = userDao.findByEmail(email);
        if (user == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮箱错误"));
            return;
        }

        if (user.getUserStatus() != 0) {
            JsonUtil.writeJsonToResponse(response, Result.error("账号被锁定"));
            return;
        }

        if (!Md5Util.matches(password, user.getPassword())) {
            JsonUtil.writeJsonToResponse(response, Result.error("密码错误"));
            return;
        }

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_USER");
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        String token = JwtUtil.generateToken(claims);

        // 将令牌存入Redis，有效期6小时
        redisService.set(token, token, 6 * 60 * 60);

        JsonUtil.writeJsonToResponse(response, Result.success("登录成功", token));
    }

    /**
     * 获取用户信息
     * GET /user/getUserInfo
     */
    private void getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");

        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        User user = userDao.findById(userId);

        if (user == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户不存在"));
            return;
        }

        // 构建返回数据（与原项目UserVO一致）
        Map<String, Object> userVO = new HashMap<>();
        userVO.put("userId", user.getUserId());
        userVO.put("username", user.getUsername());
        userVO.put("phone", user.getPhone());
        userVO.put("email", user.getEmail());
        userVO.put("userAvatar", user.getUserAvatar());
        userVO.put("introduction", user.getIntroduction());
        userVO.put("createTime", user.getCreateTime());

        JsonUtil.writeJsonToResponse(response, Result.success(userVO));
    }

    /**
     * 更新用户信息
     * PUT /user/updateUserInfo
     */
    private void updateUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");

        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        User user = new User();
        user.setUserId(userId);
        user.setUsername(params.get("username"));
        user.setPhone(params.get("phone"));
        user.setEmail(params.get("email"));
        user.setIntroduction(params.get("introduction"));

        int result = userDao.update(user);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("更新失败"));
        }
    }

    /**
     * 更新用户头像
     * PATCH /user/updateUserAvatar
     * 注意：原项目使用MultipartFile上传，这里简化为接收avatarUrl
     */
    private void updateUserAvatar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");

        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();

        // 注意：原项目是上传文件到MinIO，这里简化处理
        // 实际使用时需要处理文件上传
        String avatarUrl = request.getParameter("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            JsonUtil.writeJsonToResponse(response, Result.error("头像URL不能为空"));
            return;
        }

        int result = userDao.updateAvatar(userId, avatarUrl);
        if (result > 0) {
            JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("更新失败"));
        }
    }

    /**
     * 更新用户密码
     * PATCH /user/updateUserPassword
     * 请求体: { oldPassword, newPassword, repeatPassword }
     */
    private void updateUserPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");

        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String repeatPassword = params.get("repeatPassword");

        if (oldPassword == null || newPassword == null || repeatPassword == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数不完整"));
            return;
        }

        User user = userDao.findById(userId);
        if (user == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("用户不存在"));
            return;
        }

        if (!Md5Util.matches(oldPassword, user.getPassword())) {
            JsonUtil.writeJsonToResponse(response, Result.error("原密码填写不正确"));
            return;
        }

        if (Md5Util.matches(newPassword, user.getPassword())) {
            JsonUtil.writeJsonToResponse(response, Result.error("新密码不能与原密码相同"));
            return;
        }

        if (!newPassword.equals(repeatPassword)) {
            JsonUtil.writeJsonToResponse(response, Result.error("两次填写的新密码不一样"));
            return;
        }

        int result = userDao.updatePassword(userId, Md5Util.md5(newPassword));
        if (result > 0) {
            // 注销当前令牌
            String token = (String) request.getAttribute("token");
            if (token != null) {
                redisService.delete(token);
            }
            JsonUtil.writeJsonToResponse(response, Result.success("更新成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("更新失败"));
        }
    }

    /**
     * 重置用户密码
     * PATCH /user/resetUserPassword
     * 请求体: { email, verificationCode, newPassword, repeatPassword }
     */
    private void resetUserPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = JsonUtil.readJsonFromRequest(request, Map.class);

        String email = params.get("email");
        String verificationCode = params.get("verificationCode");
        String newPassword = params.get("newPassword");
        String repeatPassword = params.get("repeatPassword");

        if (email == null || verificationCode == null || newPassword == null || repeatPassword == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("参数不完整"));
            return;
        }

        // 验证验证码
        String storedCode = redisService.get("verificationCode:" + email);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            JsonUtil.writeJsonToResponse(response, Result.error("验证码无效"));
            return;
        }

        if (!newPassword.equals(repeatPassword)) {
            JsonUtil.writeJsonToResponse(response, Result.error("两次填写的新密码不一样"));
            return;
        }

        User user = userDao.findByEmail(email);
        if (user == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("邮箱不存在"));
            return;
        }

        int result = userDao.updatePasswordByEmail(email, Md5Util.md5(newPassword));
        if (result > 0) {
            redisService.delete("verificationCode:" + email);
            JsonUtil.writeJsonToResponse(response, Result.success("密码重置成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("密码重置失败"));
        }
    }

    /**
     * 用户登出
     * POST /user/logout
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null) {
            Boolean deleted = redisService.delete(token) > 0;
            if (deleted) {
                JsonUtil.writeJsonToResponse(response, Result.success("登出成功"));
            } else {
                JsonUtil.writeJsonToResponse(response, Result.error("登出失败"));
            }
        } else {
            JsonUtil.writeJsonToResponse(response, Result.success("登出成功"));
        }
    }

    /**
     * 注销账号
     * DELETE /user/deleteAccount
     */
    private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");

        if (claims == null) {
            JsonUtil.writeJsonToResponse(response, Result.error("未登录"));
            return;
        }

        Long userId = ((Number) claims.get("userId")).longValue();

        int result = userDao.deleteById(userId);
        if (result > 0) {
            String token = (String) request.getAttribute("token");
            if (token != null) {
                redisService.delete(token);
            }
            JsonUtil.writeJsonToResponse(response, Result.success("删除成功"));
        } else {
            JsonUtil.writeJsonToResponse(response, Result.error("删除失败"));
        }
    }
}
