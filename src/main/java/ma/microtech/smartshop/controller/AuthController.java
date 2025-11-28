package ma.microtech.smartshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.exception.UnauthorizedException;
import ma.microtech.smartshop.service.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    record LoginRequest(String username, String password){}
    record LoginResponse(Long id, String username, String role){}
    record LogoutResponse(String message){}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest req){
        User user = authService.login(loginRequest.username(), loginRequest.password(), req);

        LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req){
        if(authService.getCurrentUser(req) == null){
            throw new UnauthorizedException("You are not authenticated");
        }
        authService.logout(req);
        return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
    }
}
