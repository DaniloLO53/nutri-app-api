package org.nutri.app.nutri_app_api.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.services.RoleUsernamePasswordAuthToken;
import org.nutri.app.nutri_app_api.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenJwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PathMatcher pathMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (pathMatcher.match("/api/auth/**", request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtUtils.getJwtFromCookies(request);

        if (jwtToken != null) {
            Claims claims = jwtUtils.validateAndParseClaims(jwtToken);
            String requestUserEmail = claims.getSubject();

            RoleName role = RoleName.valueOf(claims.get("role", String.class));

            UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(requestUserEmail, role);
            UsernamePasswordAuthenticationToken authentication =
                    new RoleUsernamePasswordAuthToken(
                            userDetails,
                            null,
                            role,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }
}
