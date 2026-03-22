package com.quazzom.mastermind.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.dto.LoginResponse;
import com.quazzom.mastermind.dto.MeResponse;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.exception.UnauthorizedUserOrPasswordInvalidException;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.validator.LoginRequestValidator;
import com.quazzom.mastermind.validator.RegisterRequestValidator;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final RegisterRequestValidator registerRequestValidator;
	private final LoginRequestValidator loginRequestValidator;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public AuthService(
			UserRepository userRepository,
			RegisterRequestValidator registerRequestValidator,
			LoginRequestValidator loginRequestValidator,
			JwtService jwtService,
			PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.registerRequestValidator = registerRequestValidator;
		this.loginRequestValidator = loginRequestValidator;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
	}

	public RegisterResponse register(RegisterRequest request) {

		registerRequestValidator.validateRequestBody(request);

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
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user = userRepository.save(user);

		return new RegisterResponse(user);
	}

	public LoginResponse login(LoginRequest request) {

		loginRequestValidator.validateRequestBody(request);

		String identifier = request.getUsername();
		User user = userRepository.findByEmail(identifier)
				.or(() -> userRepository.findByNickname(identifier))
				.orElseThrow(() -> new UnauthorizedUserOrPasswordInvalidException());

		boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
		if (!passwordMatch) {
			throw new UnauthorizedUserOrPasswordInvalidException();
		}

		String token = jwtService.generateToken(user.getUuidPublic());
		return new LoginResponse(token);
	}

	public MeResponse me(UUID uuidPublic) {

		User user = userRepository.findByUuidPublic(uuidPublic)
				.orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));

		return new MeResponse(user);
	}
}
