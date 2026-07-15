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
            "/api/trace/qrcode/detail/", "/api/geocode", "/api/presale/list",
            "/api/trace/detail/", "/api/farmer/list", "/api/farmer/detail/",
            "/api/comment/product/", "/api/marketing/product/", "/api/marketing/active",
            "/api/points/exchange/rules"
    );
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthInterceptor(JwtUtil jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 绂佹娴忚鍣ㄧ紦瀛楢PI鍝嶅簲
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        String url = request.getRequestURI();

        // 椤甸潰璇锋眰锛堥潪API锛夎烦杩囬壌鏉?
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/html")) return true;

        // 鍏紑鎺ュ彛
        for (String p : PUBLIC_PATHS) {
            if (url.equals(p)) return true;
        }
        for (String p : PUBLIC_PREFIXES) {
            if (url.startsWith(p)) return true;
        }

        // 闈欐€佽祫婧愬拰SPA璺敱
        if (!url.startsWith("/api/") && !url.startsWith("/admin/")) return true;

        // 鎻愬彇token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, 401, "鏈櫥褰曪紝璇峰厛鐧诲綍");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            writeError(response, 401, "鐧诲綍宸茶繃鏈燂紝璇烽噸鏂扮櫥褰?);
            return false;
        }

        // 灏嗙敤鎴蜂俊鎭瓨鍏equest鍜孶serContext
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
