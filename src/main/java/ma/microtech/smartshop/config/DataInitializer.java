package ma.microtech.smartshop.config;

import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.enums.UserRole;
import ma.microtech.smartshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {

            User admin = User.builder()
                    .username("admin")
                    .password("admin")
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);

        }
    }
}