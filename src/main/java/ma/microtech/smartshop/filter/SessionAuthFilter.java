package ma.microtech.smartshop.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.exception.UnauthorizedException;
import ma.microtech.smartshop.service.interfaces.AuthService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class SessionAuthFilter implements Filter {
    private final AuthService authService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getRequestURI();

        if(path.startsWith("/api/auth/") || path.equals("/")){
            chain.doFilter(request, response);
            return;
        }

        if(!authService.isAuthenticated(request)){
            throw new UnauthorizedException("Authentication required");
        }

        User currentUser = authService.getCurrentUser(request);
        request.setAttribute("currentUser", currentUser);

        if(authService.hasRole(request, "ADMIN")){
            chain.doFilter(request, response);
            return;
        }

        if(authService.hasRole(request, "CLIENT")){
            boolean allowed = isClientAllowedPath(path, request.getMethod());
            if(!allowed){
                throw new ForbiddenException("Access Denied for Client Role");
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isClientAllowedPath(String path, String method){
        return path.matches("/api/clients/me");
    }
}
