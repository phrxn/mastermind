package com.quazzom.mastermind.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.dto.UserProfileResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.validator.UserPasswordRequestValidator;
import com.quazzom.mastermind.validator.UserProfileRequestValidator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRequestValidator userProfileRequestValidator;
    private final UserPasswordRequestValidator userPasswordRequestValidator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            UserProfileRequestValidator userProfileRequestValidator,
            UserPasswordRequestValidator userPasswordRequestValidator,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRequestValidator = userProfileRequestValidator;
        this.userPasswordRequestValidator = userPasswordRequestValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(UUID uuidPublic) {
        User user = getAuthenticatedUser(uuidPublic);
        return new UserProfileResponse(user);
    }

    public UserProfileResponse updateProfile(UUID uuidPublic, UserProfileRequest request) {

        userProfileRequestValidator.validateRequestBody(request);

        User user = getAuthenticatedUser(uuidPublic);

        userRepository.findByNickname(request.getNickname())
                .filter(foundUser -> !foundUser.getUuidPublic().equals(uuidPublic))
                .ifPresent(foundUser -> {
                    throw new UserAlreadyExistsException("O nickname já está em uso");
                });

        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setAge(request.getAge());

        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    public void deleteProfile(UUID uuidPublic) {
        User user = getAuthenticatedUser(uuidPublic);
        userRepository.delete(user);
    }

    public void updatePassword(UUID uuidPublic, UserPasswordRequest request) {
        userPasswordRequestValidator.validateRequestBody(request);

        User user = getAuthenticatedUser(uuidPublic);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Senha atual inválida");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getAuthenticatedUser(UUID uuidPublic) {
        return userRepository.findByUuidPublic(uuidPublic)
                .orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));
    }
}