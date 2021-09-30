package ru.mirea.movieguide.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.User;
import ru.mirea.movieguide.repository.UserRepository;
import ru.mirea.movieguide.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTFilter extends GenericFilterBean {
    private final JWTProvider provider;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (provider.validateToken(token)) {
                    try {
                        User user = userService.findUserById(provider.getUserId(token));
                        UsernamePasswordAuthenticationToken authToken
                                = new UsernamePasswordAuthenticationToken(user, null,
                                user.getRoles().stream().map(role ->
                                        new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList())
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                } else ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (!((HttpServletRequest) servletRequest).getMethod().equals(HttpMethod.GET.name()))
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else if (!((HttpServletRequest) servletRequest).getMethod().equals(HttpMethod.GET.name()))
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        else
            filterChain.doFilter(servletRequest, servletResponse);
    }
}
