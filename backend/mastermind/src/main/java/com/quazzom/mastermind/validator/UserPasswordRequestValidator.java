package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

@Component
public class UserPasswordRequestValidator implements RequestValidator<UserPasswordRequest> {

    private final MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;
    private final UserBusinessRule userBusinessRule;

    public UserPasswordRequestValidator() {
        this(new MessageDefaultForPropertiesJSON(), new UserBusinessRule("abc", "a@a.com", "abcd", 1, "Abc@1224"));
    }

    public UserPasswordRequestValidator(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON,
            UserBusinessRule userBusinessRule) {
        this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
        this.userBusinessRule = userBusinessRule;
    }

    @Override
    public void validateRequestBody(UserPasswordRequest request) {
        isCurrentPasswordValid(request.getCurrentPassword());
		isNewPasswordValid(request.getNewPassword());
        userBusinessRule.setPassword(request.getNewPassword(), true);
    }

    private void isCurrentPasswordValid(String currentPassword) {
        if (currentPassword == null) {
            throw new RequestPropertyNotFoundException(
                    messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("currentPassword"));
        }

        if (currentPassword.trim().isEmpty()) {
            throw new RequestInvalidPropertyValueException(
                    "O campo 'currentPassword' deve ser preenchido, ele não pode estar vazio");
        }
    }

	public void isNewPasswordValid(String newPassword) {
        if (newPassword == null) {
            throw new RequestPropertyNotFoundException(
                    messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("newPassword"));
        }
		userBusinessRule.setPassword(newPassword, true);
	}
}