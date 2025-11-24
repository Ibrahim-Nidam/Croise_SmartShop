package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.repository.UserRepository;
import ma.microtech.smartshop.service.interfaces.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(String username, String password, HttpServletRequest req){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Invalid Credentials"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        return user;
    }

    @Override
    public void logout(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        if(session != null){
            session.invalidate();
        }
    }

    @Override
    public User getCurrentUser(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        if(session == null) return null;

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) return null;

        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    @Override
    public boolean hasRole(HttpServletRequest request, String role) {
        User user = getCurrentUser(request);
        return user != null && role.equals(user.getRole().name());
    }
}
