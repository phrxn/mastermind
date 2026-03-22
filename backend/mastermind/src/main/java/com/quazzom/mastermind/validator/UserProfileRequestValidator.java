package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.dto.UserProfileRequest;

@Component
public class UserProfileRequestValidator implements RequestValidator<UserProfileRequest> {

    private final UserBusinessRule userBusinessRule;

    public UserProfileRequestValidator() {
        this(new UserBusinessRule("abc", "a@a.com", "abcd", 1, "Abc@1224"));
    }

    public UserProfileRequestValidator(UserBusinessRule userBusinessRule) {
        this.userBusinessRule = userBusinessRule;
    }

    @Override
    public void validateRequestBody(UserProfileRequest request) {
        userBusinessRule.setName(request.getName());
        userBusinessRule.setNickname(request.getNickname());
        userBusinessRule.setAge(request.getAge());
    }
}