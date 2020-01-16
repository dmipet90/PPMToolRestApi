package ee.dmipet90.ppmtool.security;

import ee.dmipet90.ppmtool.domain.User;
import ee.dmipet90.ppmtool.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static ee.dmipet90.ppmtool.security.SecurityConstants.HEADER_STRING;
import static ee.dmipet90.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
           String jwt = getJwtFromRequest(httpServletRequest);
           if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
               Long userId = tokenProvider.getUserIdFromJWT(jwt);
               User userDetails = customUserDetailsService.loadUserById(userId);
               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                       userDetails, null, Collections.emptyList() // <- Roles Here if u have it
               );
               authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
               SecurityContextHolder.getContext().setAuthentication(authenticationToken);

           }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
