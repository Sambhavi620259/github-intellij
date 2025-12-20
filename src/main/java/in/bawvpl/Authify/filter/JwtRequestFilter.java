package in.bawvpl.Authify.filter;

import in.bawvpl.Authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // All endpoints that NEVER require authentication
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/v1.0/register",
            "/api/v1.0/login",
            "/api/v1.0/login/verify-otp",
            "/api/v1.0/verify-otp",
            "/api/v1.0/send-otp",
            "/api/v1.0/send-reset-otp",
            "/api/v1.0/reset-password",

            // Swagger
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        // Allow all OPTIONS requests (important for CORS & browsers)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Log to help troubleshoot
        log.info("JWT Filter checking path: {}", path);

        // Allow all public paths (with or without trailing slash)
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) ||
                    path.startsWith(publicPath + "/") ||
                    path.startsWith(publicPath)) {
                log.info("PUBLIC endpoint allowed without JWT → {}", publicPath);
                return true;
            }
        }

        // Otherwise JWT is required
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No Authorization header → continue normally (Spring Security will block protected routes)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            unauthorized(response, "Invalid token");
            return;
        }

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                unauthorized(response, "Token expired or invalid");
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{ \"error\": \"Unauthorized\", \"message\": \"" + message + "\" }"
        );
    }
}
