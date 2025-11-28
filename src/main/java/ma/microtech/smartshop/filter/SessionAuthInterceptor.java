package ma.microtech.smartshop.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.exception.UnauthorizedException;
import ma.microtech.smartshop.service.interfaces.AuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class SessionAuthInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getRequestURI();

        if(path.startsWith("/api/auth/") || path.equals("/")){
            return true;
        }

        if(!authService.isAuthenticated(request)){
            throw new UnauthorizedException("Authentication required");
        }

        User currentUser = authService.getCurrentUser(request);
        request.setAttribute("currentUser", currentUser);

        if(authService.hasRole(request, "ADMIN")){
            return true;
        }

        if(authService.hasRole(request, "CLIENT")){
            boolean allowed = isClientAllowedPath(path, request.getMethod());
            if(!allowed){
                throw new ForbiddenException("Access Denied for Client Role");
            }
        }
        return true;
    }

    private boolean isClientAllowedPath(String path, String method){
        return path.matches("/api/clients/me") ||
                path.matches("/api/clients/me/orders") ||
                path.matches("/api/products") ||
                path.matches("^/api/payments/orders/\\d+/payments$");
    }
}