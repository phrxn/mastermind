package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

@Component
public class LoginRequestValidator implements RequestValidator<LoginRequest> {

	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

	public LoginRequestValidator() {
		this(new MessageDefaultForPropertiesJSON());
	}

	public LoginRequestValidator(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
	}

	@Override
	public void validateRequestBody(LoginRequest request) {
		isUsernameValid(request.getUsername());
		isPasswordValid(request.getPassword());
	}

	public void isUsernameValid(String username) {
		if (username == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("username"));
		}

		username = username.trim();

		if (username.isEmpty()) {
			throw new RequestInvalidPropertyValueException(
					"O campo 'username' deve ser preenchido, ele não pode estar vazio");
		}
	}

	public void isPasswordValid(String password) {
		if (password == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("password"));
		}

		password = password.trim();

		if (password.isEmpty()) {
			throw new RequestInvalidPropertyValueException(
					"O campo 'password' deve ser preenchido, ele não pode estar vazio");
		}
	}
}
