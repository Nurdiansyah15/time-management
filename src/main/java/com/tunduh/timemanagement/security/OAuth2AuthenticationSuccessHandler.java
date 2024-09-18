package com.tunduh.timemanagement.security;

import com.tunduh.timemanagement.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private static final String SWAGGER_REDIRECT_URL = "/swagger-ui/oauth2-redirect.html";

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
                                              HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        logger.info("OAuth2AuthenticationSuccessHandler initialized");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("OAuth2 Authentication Success Handler triggered");
        logRequestDetails(request);
        logAuthenticationDetails(authentication);

        try {
            String token = tokenProvider.createTokenFromAuthentication(authentication);
            logger.debug("JWT token created successfully: {}", token);

            String targetUrl = determineTargetUrl(token);

            if (response.isCommitted()) {
                logger.warn("Response has already been committed. Unable to redirect to {}", targetUrl);
                return;
            }

            clearAuthenticationAttributes(request, response);

            if (isSwaggerRequest(request)) {
                handleSwaggerRedirect(response, token);
            } else {
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            }
        } catch (Exception e) {
            logger.error("An error occurred during authentication success handling", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication");
        }
    }

    private String determineTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();
    }

    private void handleSwaggerRedirect(HttpServletResponse response, String token) throws IOException {
        logger.debug("Handling Swagger redirect");
        String redirectUrl = UriComponentsBuilder.fromUriString(SWAGGER_REDIRECT_URL)
                .queryParam("token", token)
                .build().toUriString();
        logger.debug("Swagger redirect URL: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Clearing authentication attributes");
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        logger.debug("Authentication attributes cleared");
    }

    private boolean isSwaggerRequest(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        boolean isSwagger = referer != null && referer.contains("swagger-ui");
        logger.debug("Is Swagger request: {}. Referer: {}", isSwagger, referer);
        return isSwagger;
    }

    private void logRequestDetails(HttpServletRequest request) {
        logger.debug("Request URI: {}", request.getRequestURI());
        logger.debug("Request URL: {}", request.getRequestURL());
        logger.debug("Referer: {}", request.getHeader("Referer"));
        logger.debug("User Agent: {}", request.getHeader("User-Agent"));
    }

    private void logAuthenticationDetails(Authentication authentication) {
        logger.debug("Authentication Principal: {}", authentication.getPrincipal());
        logger.debug("Authentication Authorities: {}", authentication.getAuthorities());
    }
}