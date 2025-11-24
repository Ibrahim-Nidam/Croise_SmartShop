package ma.microtech.smartshop.config;

import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.enums.UserRole;
import ma.microtech.smartshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            var encoder = new BCryptPasswordEncoder();

            User admin = User.builder()
                    .username("admin")
                    .password(encoder.encode("admin"))
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);

        }
    }
}