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
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {

        // 放行浏览器跨域预检；跨域配置仍需单独设置
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {
            return unauthorized(response);
        }

        String token = authorization.substring(7).trim();

        try {
            // 你现有的方法会验证签名和过期时间
            Claims claims = JwtUtils.parseJWT(token);

            Object id = claims.get("id");
            if (!(id instanceof Number)) {
                return unauthorized(response);
            }

            long userId = ((Number) id).longValue();
            if (userId <= 0) {
                return unauthorized(response);
            }

            // 当前请求内保存用户身份
            request.setAttribute("currentUserId", userId);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(response);
        }
    }

    private boolean unauthorized(
            HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"code\":0,\"msg\":\"请先登录或重新登录\",\"data\":null}"
        );
        return false;
    }
}