package cn.edu.seig.vibemusic.util;

import com.google.gson.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON工具类
 * 用于处理JSON序列化和反序列化
 */
public class JsonUtil {

    // 日期时间格式化器
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Gson实例，配置日期格式和LocalDateTime序列化
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            // 注册LocalDateTime序列化器
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_TIME_FORMATTER)))
            // 注册LocalDateTime反序列化器
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER))
            // 注册LocalDate序列化器
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_FORMATTER)))
            // 注册LocalDate反序列化器
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DATE_FORMATTER))
            .create();

    /**
     * 将对象转换为JSON字符串
     * @param obj 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 将JSON字符串转换为对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 将JSON字符串转换为对象（支持泛型）
     * @param json JSON字符串
     * @param type 目标类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 从请求中读取JSON数据
     * @param request HTTP请求对象
     * @return JSON字符串
     */
    public static String readJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 从请求中读取JSON并转换为对象
     * @param request HTTP请求对象
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    public static <T> T readJsonFromRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        String json = readJsonFromRequest(request);
        return fromJson(json, clazz);
    }

    /**
     * 向响应中写入JSON数据
     * @param response HTTP响应对象
     * @param obj 要写入的对象
     */
    public static void writeJsonToResponse(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(toJson(obj));
            writer.flush();
        }
    }
}

