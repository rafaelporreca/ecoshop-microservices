package com.ecoshop.userservice.service;

import com.ecoshop.userservice.dto.UserRequest;
import com.ecoshop.userservice.model.User;
import com.ecoshop.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j // Para logs
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Long registerUser(UserRequest userRequest) {
        // Validação básica (poderia ser isolada)
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        // Mapping (DTO -> Entity)
        User user = User.builder()
                .fullName(userRequest.fullName())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password())) // Regra de negócio
                .roles(new HashSet<>(Set.of("ROLE_USER"))) // Regra de negócio
                .build();

        userRepository.save(user);
        User savedUser = userRepository.save(user); // O save retorna o objeto com ID preenchido
        log.info("User {} registered successfully", savedUser.getId());
        return savedUser.getId();
    }
}
