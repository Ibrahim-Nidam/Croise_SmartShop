package ma.microtech.smartshop.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ma.microtech.smartshop.entity.User;

public interface AuthService {
    User login(String username, String password, HttpServletRequest req);
    void logout(HttpServletRequest req);
    User getCurrentUser(HttpServletRequest req);
}
