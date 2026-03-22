package com.quazzom.mastermind.unit.service;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.dto.LoginResponse;
import com.quazzom.mastermind.dto.MeResponse;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.service.AuthService;
import com.quazzom.mastermind.service.JwtService;
import com.quazzom.mastermind.validator.LoginRequestValidator;
import com.quazzom.mastermind.validator.RegisterRequestValidator;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RegisterRequestValidator registerRequestValidator;

	@Mock
	private LoginRequestValidator loginRequestValidator;

	@Mock
	private JwtService jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Captor
	private ArgumentCaptor<User> userCaptor;

	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;

	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setName("Maria Silva");
		registerRequest.setEmail("maria@teste.com");
		registerRequest.setNickname("maria");
		registerRequest.setAge(25);
		registerRequest.setPassword("Abc123!");

		loginRequest = new LoginRequest();
		loginRequest.setUsername("maria@teste.com");
		loginRequest.setPassword("Abc123!");
	}

	// ===== register() =====
	@Test
	void registerShouldValidateSaveAndReturnResponse() {
		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
		when(userRepository.existsByNickname(registerRequest.getNickname())).thenReturn(false);
		when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

		User savedUser = new User();
		savedUser.setId(10L);
		savedUser.setName(registerRequest.getName());
		savedUser.setEmail(registerRequest.getEmail());
		savedUser.setNickname(registerRequest.getNickname());
		savedUser.setAge(registerRequest.getAge());
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		RegisterResponse response = authService.register(registerRequest);

		verify(registerRequestValidator).validateRequestBody(registerRequest);
		verify(userRepository).save(userCaptor.capture());

		User capturedUser = userCaptor.getValue();
		assertEquals(registerRequest.getName(), capturedUser.getName());
		assertEquals(registerRequest.getEmail(), capturedUser.getEmail());
		assertEquals(registerRequest.getNickname(), capturedUser.getNickname());
		assertEquals(registerRequest.getAge(), capturedUser.getAge());
		assertEquals("encodedPassword", capturedUser.getPassword());

		assertEquals(savedUser.getName(), response.getName());
		assertEquals(savedUser.getEmail(), response.getEmail());
		assertEquals(savedUser.getNickname(), response.getNickname());
		assertEquals(savedUser.getAge(), response.getAge());
	}

	@Test
	void registerShouldThrowWhenEmailAlreadyExists() {
		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

		UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
				() -> authService.register(registerRequest));

		assertEquals("O e-mail já está em uso", exception.getMessage());
		verify(registerRequestValidator).validateRequestBody(registerRequest);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void registerShouldThrowWhenNicknameAlreadyExists() {
		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
		when(userRepository.existsByNickname(registerRequest.getNickname())).thenReturn(true);

		UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
				() -> authService.register(registerRequest));

		assertEquals("O nickname já está em uso", exception.getMessage());
		verify(registerRequestValidator).validateRequestBody(registerRequest);
		verify(userRepository, never()).save(any(User.class));
	}

	// ===== login() =====
	@Test
	void loginShouldReturnTokenWhenCredentialsAreValid() {
		User user = new User();
		user.setId(10L);
		UUID uuidPublic = UUID.randomUUID();
		user.setUuidPublic(uuidPublic);
		user.setEmail("maria@teste.com");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtService.generateToken(uuidPublic)).thenReturn("jwt-token");

		LoginResponse response = authService.login(loginRequest);

		assertEquals("jwt-token", response.getToken());
		assertEquals("Bearer", response.getTokenType());
	}

	@Test
	void loginShouldReturnTokenWhenNicknameIsUsedAndCredentialsAreValid() {
		User user = new User();
		user.setId(10L);
		UUID uuidPublic = UUID.randomUUID();
		user.setUuidPublic(uuidPublic);
		user.setEmail("maria@teste.com");
		user.setPassword("encodedPassword");

		loginRequest.setUsername("maria");
		when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.empty());
		when(userRepository.findByNickname(loginRequest.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtService.generateToken(uuidPublic)).thenReturn("jwt-token");

		LoginResponse response = authService.login(loginRequest);

		assertEquals("jwt-token", response.getToken());
		assertEquals("Bearer", response.getTokenType());
	}

	@Test
	void loginShouldThrowWhenUserDoesNotExist() {
		when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.empty());
		when(userRepository.findByNickname(loginRequest.getUsername())).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> authService.login(loginRequest));

		assertEquals("Usuário ou senha inválidos", exception.getMessage());
	}

	@Test
	void loginShouldThrowWhenPasswordDoesNotMatch() {
		User user = new User();
		user.setEmail("maria@teste.com");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> authService.login(loginRequest));

		assertEquals("Usuário ou senha inválidos", exception.getMessage());
	}

	// ===== me() =====
	@Test
	void meShouldReturnAuthenticatedUserData() {
		User user = new User();
		user.setId(10L);
		UUID uuidPublic = UUID.randomUUID();
		user.setUuidPublic(uuidPublic);
		user.setName("Maria Silva");
		user.setEmail("maria@teste.com");
		user.setNickname("maria");
		user.setAge(25);

		when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));

		MeResponse response = authService.me(uuidPublic);

		assertTrue(response.isAuthenticated());
		assertEquals("Maria Silva", response.getName());
		assertEquals("maria@teste.com", response.getEmail());
		assertEquals("maria", response.getNickname());
		assertEquals(25, response.getAge());
	}

	@Test
	void meShouldThrowWhenUserFromTokenIsNotFound() {
		UUID unknownUuid = UUID.randomUUID();
		when(userRepository.findByUuidPublic(unknownUuid)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> authService.me(unknownUuid));

		assertEquals("Usuário não autenticado", exception.getMessage());
	}
}