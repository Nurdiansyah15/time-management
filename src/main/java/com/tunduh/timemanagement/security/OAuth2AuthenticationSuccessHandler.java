package com.tunduh.timemanagement.security;

import com.tunduh.timemanagement.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.tunduh.timemanagement.utils.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "oauth2_auth_request_redirect_uri";
    private static final String SWAGGER_REDIRECT_URL = "/swagger-ui/oauth2-redirect.html";

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${app.oauth2.authorizedRedirectUris}")
    private List<String> authorizedRedirectUris;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
                                              HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        logger.info("OAuth2AuthenticationSuccessHandler initialized");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("OAuth2 Authentication Success Handler triggered");
        logRequestDetails(request);
        logAuthenticationDetails(authentication);

        try {
            String targetUrl = determineTargetUrl(request, response, authentication);
            if (response.isCommitted()) {
                logger.warn("Response has already been committed. Unable to redirect to {}", targetUrl);
                return;
            }

            clearAuthenticationAttributes(request, response);
            String token = tokenProvider.createToken(authentication);
            logger.debug("JWT token created successfully");

            if (isSwaggerRequest(request)) {
                handleSwaggerRedirect(response, token);
            } else {
                handleRegularRedirect(request, response, targetUrl, token);
            }
        } catch (Exception e) {
            logger.error("An error occurred during authentication success handling", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication");
        }
    }

    private void handleSwaggerRedirect(HttpServletResponse response, String token) throws IOException {
        logger.debug("Handling Swagger redirect");
        String redirectUrl = UriComponentsBuilder.fromUriString(SWAGGER_REDIRECT_URL)
                .queryParam("access_token", token)
                .build().toUriString();
        logger.info("Redirecting to Swagger UI with token: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    private void handleRegularRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, String token) throws IOException {
        logger.debug("Handling regular redirect. Target URL: {}", targetUrl);
        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
        logger.info("Redirecting to: {}", redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.debug("Determining target URL");
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            logger.warn("Unauthorized redirect URI: {}", redirectUri.get());
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        logger.debug("Determined target URL: {}", targetUrl);
        return targetUrl;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Clearing authentication attributes");
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        logger.debug("Authentication attributes cleared");
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        logger.debug("Checking if URI is authorized: {}", uri);
        try {
            URI clientRedirectUri = new URI(uri);
            boolean isAuthorized = authorizedRedirectUris
                    .stream()
                    .anyMatch(authorizedRedirectUri -> {
                        URI authorizedURI = URI.create(authorizedRedirectUri);
                        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                                && authorizedURI.getPort() == clientRedirectUri.getPort();
                    });
            logger.debug("URI {} is authorized: {}", uri, isAuthorized);
            return isAuthorized;
        } catch (Exception e) {
            logger.error("Error parsing URI: {}", uri, e);
            return false;
        }
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