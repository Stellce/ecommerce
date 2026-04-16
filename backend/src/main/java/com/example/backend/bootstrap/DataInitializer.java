package com.example.backend.bootstrap;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.user.RoleRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${app.bootstrap-admin.email}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if(adminEmail == null || adminEmail.isBlank()) return;
        if(adminPassword == null || adminPassword.isBlank()) return;

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    adminEmail,
                    encoder.encode(adminPassword),
                    Set.of(roleRepository.findByName("ADMIN")
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)))
            );
            userRepository.save(admin);
        }
    }
}
