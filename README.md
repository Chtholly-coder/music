# Vibe Music Server - 纯Servlet版本

这是一个将Spring Boot项目解构为纯Servlet实现的音乐服务器后端，与原项目接口完全兼容，可与前端无缝衔接。

## 项目结构

```
vibe-music-servlet/
├── pom.xml                          # Maven配置文件
├── src/main/java/cn/edu/seig/vibemusic/
│   ├── config/                      # 配置类
│   │   ├── DatabaseConfig.java      # 数据库连接池配置（HikariCP）
│   │   └── RedisConfig.java         # Redis连接池配置（Jedis）
│   ├── dao/                         # 数据访问层（原生JDBC）
│   │   ├── UserDao.java             # 用户数据访问
│   │   ├── SongDao.java             # 歌曲数据访问
│   │   ├── ArtistDao.java           # 歌手数据访问
│   │   ├── PlaylistDao.java         # 歌单数据访问
│   │   ├── AdminDao.java            # 管理员数据访问
│   │   ├── UserFavoriteDao.java     # 用户收藏数据访问
│   │   └── BannerDao.java           # 轮播图数据访问
│   ├── filter/                      # 过滤器
│   │   ├── CorsFilter.java          # 跨域过滤器
│   │   ├── AuthFilter.java          # JWT认证过滤器
│   │   └── CharacterEncodingFilter.java # 字符编码过滤器
│   ├── listener/
│   │   └── AppContextListener.java  # 应用上下文监听器
│   ├── model/entity/                # 实体类
│   ├── result/                      # 响应结果类
│   ├── service/                     # 服务类
│   ├── servlet/                     # Servlet控制器
│   │   ├── UserServlet.java         # 用户接口 /user/*
│   │   ├── SongServlet.java         # 歌曲接口 /song/*
│   │   ├── ArtistServlet.java       # 歌手接口 /artist/*
│   │   ├── PlaylistServlet.java     # 歌单接口 /playlist/*
│   │   ├── AdminServlet.java        # 管理员接口 /admin/*
│   │   ├── FavoriteServlet.java     # 收藏接口 /favorite/*
│   │   └── BannerServlet.java       # 轮播图接口 /banner/*
│   └── util/                        # 工具类
└── src/main/webapp/WEB-INF/web.xml
```

## API接口（与原项目完全一致）

### 用户接口 `/user/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | /user/sendVerificationCode?email=xxx | 发送验证码 | 否 |
| POST | /user/register | 用户注册 | 否 |
| POST | /user/login | 用户登录 | 否 |
| GET | /user/getUserInfo | 获取用户信息 | 是 |
| PUT | /user/updateUserInfo | 更新用户信息 | 是 |
| PATCH | /user/updateUserAvatar | 更新用户头像 | 是 |
| PATCH | /user/updateUserPassword | 更新用户密码 | 是 |
| PATCH | /user/resetUserPassword | 重置用户密码 | 否 |
| POST | /user/logout | 用户登出 | 是 |
| DELETE | /user/deleteAccount | 注销账号 | 是 |

### 歌曲接口 `/song/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | /song/getAllSongs | 分页获取歌曲列表 | 否 |
| GET | /song/getRecommendedSongs | 获取推荐歌曲 | 否 |
| GET | /song/getSongDetail/{id} | 获取歌曲详情 | 否 |

### 歌手接口 `/artist/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | /artist/getAllArtists | 分页获取歌手列表 | 否 |
| GET | /artist/getRandomArtists | 获取随机歌手 | 否 |
| GET | /artist/getArtistDetail/{id} | 获取歌手详情 | 否 |

### 歌单接口 `/playlist/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | /playlist/getAllPlaylists | 分页获取歌单列表 | 否 |
| GET | /playlist/getRecommendedPlaylists | 获取推荐歌单 | 否 |
| GET | /playlist/getPlaylistDetail/{id} | 获取歌单详情 | 否 |

### 收藏接口 `/favorite/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | /favorite/getFavoriteSongs | 获取收藏的歌曲列表 | 是 |
| POST | /favorite/collectSong?songId=xxx | 收藏歌曲 | 是 |
| DELETE | /favorite/cancelCollectSong?songId=xxx | 取消收藏歌曲 | 是 |
| POST | /favorite/getFavoritePlaylists | 获取收藏的歌单列表 | 是 |
| POST | /favorite/collectPlaylist?playlistId=xxx | 收藏歌单 | 是 |
| DELETE | /favorite/cancelCollectPlaylist?playlistId=xxx | 取消收藏歌单 | 是 |

### 轮播图接口 `/banner/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | /banner/getBannerList | 获取轮播图列表 | 否 |

### 管理员接口 `/admin/*`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | /admin/login | 管理员登录 | 否 |
| GET | /admin/getAdminInfo | 获取管理员信息 | 是 |
| POST | /admin/logout | 管理员登出 | 是 |

## 配置说明

### 数据库配置
修改 `DatabaseConfig.java`:
```java
private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vibe_music?...";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### Redis配置
修改 `RedisConfig.java`:
```java
private static final String REDIS_HOST = "127.0.0.1";
private static final int REDIS_PORT = 6379;
```

### 邮件配置
修改 `EmailService.java`:
```java
private static final String EMAIL_USERNAME = "your_email@qq.com";
private static final String EMAIL_PASSWORD = "your_auth_code";
```

## 部署说明

1. 确保已安装JDK 17+
2. 确保MySQL和Redis服务已启动
3. 导入数据库脚本 `sql/vibe_music.sql`
4. 修改配置文件中的数据库和Redis连接信息
5. 使用Maven打包：`mvn clean package`
6. 将生成的WAR文件部署到Tomcat 10+

## 技术栈对比

| 功能 | Spring Boot版本 | 纯Servlet版本 |
|------|----------------|--------------|
| 依赖注入 | @Autowired | 手动new对象 |
| 路由映射 | @RequestMapping | @WebServlet + pathInfo |
| 参数绑定 | @RequestBody | JsonUtil手动解析 |
| 数据访问 | MyBatis-Plus | 原生JDBC |
| 连接池 | Druid | HikariCP |
| JSON处理 | Jackson | Gson |
| 配置管理 | application.yml | 硬编码常量 |
| 认证拦截 | Interceptor | Filter |

## 注意事项

1. 所有接口路径和请求/响应格式与原Spring Boot项目保持一致
2. JWT令牌格式相同，可直接与前端对接
3. 响应结果格式统一为 `{ code, message, data }`
4. 文件上传功能需要额外配置MinIO服务
