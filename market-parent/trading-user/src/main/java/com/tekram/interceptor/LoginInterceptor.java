package com.tekram.interceptor;

import com.tekram.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 放行浏览器跨域预检；跨域配置仍需单独设置
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 同一路径按请求方式区分：查询商品公开，发布商品仍需登录。
        String method = request.getMethod();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // 公开查询：商品列表、商品详情
        if ("GET".equalsIgnoreCase(method) && ("/items".equals(path) || path.matches("/items/[0-9]+"))) {
            return true;
        }

        // 下面继续原来的 Token 校验
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(request, response);
        }

        String token = authorization.substring(7).trim();

        try {
            // 你现有的方法会验证签名和过期时间
            // 验证令牌并解析
            Claims claims = JwtUtils.parseJWT(token);

            Object id = claims.get("id");
            if (!(id instanceof Number)) {
                return unauthorized(request, response);
            }
            // 获取令牌里的用户 ID
            long userId = ((Number) id).longValue();
            if (userId <= 0) {
                return unauthorized(request, response);
            }

            //  保存到当前请求
            request.setAttribute("currentUserId", userId);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(request, response);
        }
    }

    private boolean unauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        boolean createAddress = "POST".equalsIgnoreCase(request.getMethod()) && "/users/me/addresses".equals(request.getRequestURI().substring(request.getContextPath().length()));
        String message = createAddress ? "AUTH_REQUIRED" : "请先登录或重新登录";
        response.getWriter().write("{\"code\":0,\"msg\":\"" + message + "\",\"data\":null}");
        return false;
    }
}
