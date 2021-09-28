package ru.mirea.movieguide.security;

import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
import java.util.Collections;

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
                        User user = userService.findUser(provider.getUserId(token));
                        UsernamePasswordAuthenticationToken authToken
                                = new UsernamePasswordAuthenticationToken(user, null,
                                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRole())));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
