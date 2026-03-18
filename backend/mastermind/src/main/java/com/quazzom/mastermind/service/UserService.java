package com.quazzom.mastermind.service;

import org.springframework.stereotype.Service;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.validator.UserValidator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public RegisterResponse register(RegisterRequest request) {

        userValidator.validateRegister(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("O e-mail já está em uso");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserAlreadyExistsException("O nickname já está em uso");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setAge(request.getAge());
        user.setPassword(request.getPassword());

        user = userRepository.save(user);

        return new RegisterResponse(user);
    }
}