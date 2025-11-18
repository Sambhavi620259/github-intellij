package in.bawvpl.Authify.filter;

import in.bawvpl.Authify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String servletPath = request.getServletPath() == null ? "" : request.getServletPath();
        String requestUri = request.getRequestURI() == null ? "" : request.getRequestURI();

        // Skip register/login paths (cover both with/without context-path and trailing slash)
        if (matchesPublic(servletPath) || matchesPublic(requestUri)) {
            return true;
        }

        return false;
    }

    private boolean matchesPublic(String path) {
        if (path == null || path.isBlank()) return false;
        if (path.equals("/api/v1.0/register") || path.equals("/register")) return true;
        if (path.equals("/api/v1.0/register/") || path.equals("/register/")) return true;
        if (path.equals("/api/v1.0/login") || path.equals("/login")) return true;
        if (path.equals("/api/v1.0/login/") || path.equals("/login/")) return true;
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) return true;
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception ex) {
                log.warn("Failed to extract username from JWT: {}", ex.getMessage());
                respondUnauthorized(response, "Invalid token: " + ex.getMessage());
                return;
            }
        } else {
            log.debug("No Bearer Authorization header for {} {}", request.getMethod(), request.getRequestURI());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Important: validate token by comparing token data with userDetails/username
                // jwtUtil.validateToken(jwt, username) should check subject and expiry
                boolean valid = jwtUtil.validateToken(jwt, username);
                if (valid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT validated and authentication set for user '{}'", username);
                } else {
                    log.warn("Invalid JWT token for user '{}'", username);
                    respondUnauthorized(response, "Invalid or expired token");
                    return;
                }
            } catch (Exception ex) {
                log.warn("Failed to validate JWT or load user details: {}", ex.getMessage());
                respondUnauthorized(response, "Invalid token or user not found");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        // escape message minimal
        String safe = message == null ? "" : message.replace("\"", "\\\"");
        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + safe + "\"}");
    }
}
