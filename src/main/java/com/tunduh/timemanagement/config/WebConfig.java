package com.tunduh.timemanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String nonce = generateNonce();
//                response.setHeader("Content-Security-Policy",
//                        "default-src 'self'; " +
//                                "script-src 'self' 'nonce-" + nonce + "' 'sha256-4IiDsMH+GkJlxivIDNfi6qk0O5HPtzyvNwVT3Wt8TIw=' https://www.google-analytics.com https://accounts.google.com https://oauth2.googleapis.com/token; " +
//                                "style-src 'self' 'unsafe-inline'; " +
//                                "img-src 'self' data: https: *.cloudinary.com; " +
//                                "font-src 'self'; " +
//                                "connect-src 'self' https://accounts.google.com *.cloudinary.com; " +
//                                "frame-src 'self' https://accounts.google.com; " +
//                                "object-src 'none';"
//                );

                response.setHeader("Content-Security-Policy", "default-src *; script-src * 'unsafe-inline' 'unsafe-eval'; style-src * 'unsafe-inline'; img-src * 'unsafe-inline' data:;");
                request.setAttribute("cspNonce", nonce);
                return true;
            }
        });
    }

    private String generateNonce() {
        // Implement a secure nonce generation method
        return "secureNonce"; // This is a placeholder, use a proper implementation
    }
}