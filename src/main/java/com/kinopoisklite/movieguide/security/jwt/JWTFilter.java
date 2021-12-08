package com.kinopoisklite.movieguide.security.jwt;

import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JWTFilter extends GenericFilterBean {
    @Autowired
    private JWTProvider provider;
    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        boolean secured = securedPathRequested(((HttpServletRequest) servletRequest).getRequestURI(),
                ((HttpServletRequest) servletRequest).getMethod());
        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (provider.validateToken(token)) {
                    User user = userService.findById(provider.getUserId(token));
                    if (user != null) {
                        UsernamePasswordAuthenticationToken authToken
                                = new UsernamePasswordAuthenticationToken(user, null,
                                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        filterChain.doFilter(servletRequest, servletResponse);
                        return;
                    } else {
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                }
            }
        }
        if (secured)
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        else
            filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean securedPathRequested(String path, String method) {
        if (method.equals(HttpMethod.GET.name()))
            return false;
        if (path.matches("/api/o?auth(.+)?"))
            return false;
        return true;
    }
}
