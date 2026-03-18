package com.quazzom.mastermind.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.service.UserService;
import com.quazzom.mastermind.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setName("Maria Silva");
        request.setEmail("maria@teste.com");
        request.setNickname("maria");
        request.setAge(25);
        request.setPassword("Abc123!");
    }

    @Test
    void registerShouldValidateSaveAndReturnResponse() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(request.getNickname())).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setNickname(request.getNickname());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegisterResponse response = userService.register(request);

        verify(userValidator).validateRegister(request);
        verify(userRepository).save(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertEquals(request.getName(), captured.getName());
        assertEquals(request.getEmail(), captured.getEmail());
        assertEquals(request.getNickname(), captured.getNickname());
        assertEquals(request.getAge(), captured.getAge());
        assertEquals(request.getPassword(), captured.getPassword());

        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(savedUser.getNickname(), response.getNickname());
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(request));

        assertEquals("O e-mail já está em uso", exception.getMessage());
        verify(userValidator).validateRegister(request);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldThrowWhenNicknameAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(request.getNickname())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(request));

        assertEquals("O nickname já está em uso", exception.getMessage());
        verify(userValidator).validateRegister(request);
        verify(userRepository, never()).save(any(User.class));
    }
}