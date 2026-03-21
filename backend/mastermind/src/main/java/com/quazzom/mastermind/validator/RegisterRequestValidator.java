package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.dto.RegisterRequest;

@Component
public class RegisterRequestValidator implements RequestValidator<RegisterRequest> {

	private UserBusinessRule userBusinessRule;

	public RegisterRequestValidator() {
		this(new UserBusinessRule("abc", "a@a.com", "abcdef", 1, "Abc@1224"));
	}

	public RegisterRequestValidator(UserBusinessRule userBusinessRule) {
		this.userBusinessRule = userBusinessRule;
	}

	@Override
	public void validateRequestBody(RegisterRequest request) {

		userBusinessRule.setName(request.getName());
		userBusinessRule.setEmail(request.getEmail());
		userBusinessRule.setNickname(request.getNickname());
		userBusinessRule.setAge(request.getAge());
		userBusinessRule.setPassword(request.getPassword());
	}
}