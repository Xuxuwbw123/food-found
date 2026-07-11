package com.freshtrace.unified.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth/login", "/auth/register", "/auth/refresh"
    );
    private static final List<String> PUBLIC_PREFIXES = Arrays.asList(
            "/api/public/", "/uploads/", "/api/product/search",
            "/api/product/recommend", "/api/product/related/",
            "/api/trace/scan/", "/api/trace/by-product/", "/api/trace/list",
            "/api/trace/detail/", "/api/farmer/list", "/api/farmer/detail/",
            "/api/comment/product/"
    );
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthInterceptor(JwtUtil jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 禁止浏览器缓存API响应
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        String url = request.getRequestURI();

        // 公开接口
        for (String p : PUBLIC_PATHS) {
            if (url.equals(p)) return true;
        }
        for (String p : PUBLIC_PREFIXES) {
            if (url.startsWith(p)) return true;
        }

        // 静态资源和SPA路由
        if (!url.startsWith("/api/") && !url.startsWith("/admin/")) return true;

        // 提取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        // 将用户信息存入request和UserContext
        Long userId = jwtUtils.getUserId(token);
        Integer userType = jwtUtils.getUserType(token);
        request.setAttribute("userId", userId);
        request.setAttribute("userType", userType);
        UserContext.setUserId(userId);
        UserContext.setUserType(userType);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
}
