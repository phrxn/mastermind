package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.exception.InvalidUserDataException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

@Component
public class UserValidator {

	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON = new MessageDefaultForPropertiesJSON();

	public UserValidator() {
		this.messageDefaultForPropertiesJSON = new MessageDefaultForPropertiesJSON();
	}

	public void validateRegister(RegisterRequest r) {

		isNameValid(r);
		isEmailValid(r);
		isNicknameValid(r);
		isAgeValid(r);
		isPasswordValid(r);

	}

	public void isNameValid(RegisterRequest r) {
		if (r.getName() == null) {
			throw new InvalidUserDataException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("name"));
		}

		if (!r.getName().matches("^[A-Za-z ]{1,60}$")) {
			throw new InvalidUserDataException(
					"O nome deve conter apenas letras e espaços, com no máximo 60 caracteres");
		}
	}

	public void isEmailValid(RegisterRequest r) {
		if (r.getEmail() == null) {
			throw new InvalidUserDataException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("email"));
		}

		String email = r.getEmail().trim();

		if (email.isEmpty()) {
			throw new InvalidUserDataException("A propriedade 'email' não pode ser vazia.");
		}

		if (email.length() > 50) {
			throw new InvalidUserDataException("O email deve ter no máximo 50 caracteres");
		}

		if (!email.matches("^[A-Za-z]+@[A-Za-z0-9.-]*[A-Za-z]\\.[A-Za-z][A-Za-z0-9.-]*$")) {
			throw new InvalidUserDataException(
					"O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)");
		}

	}

	public void isNicknameValid(RegisterRequest r) {
		if (r.getNickname() == null) {
			throw new InvalidUserDataException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("nickname"));
		}

		if (!r.getNickname().matches("^[a-z][a-z0-9]{3,19}$")) {
			throw new InvalidUserDataException(
					"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres");
		}
	}

	public void isAgeValid(RegisterRequest r) {
		if (r.getAge() == null) {
			throw new InvalidUserDataException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("age"));
		}

		if (r.getAge() < 1 || r.getAge() > 120) {
			throw new InvalidUserDataException("A idade deve ser um número entre 1 e 120");
		}
	}

	public void isPasswordValid(RegisterRequest r) {
		if (r.getPassword() == null) {
			throw new InvalidUserDataException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("password"));
		}

		if (r.getPassword().length() < 6 || r.getPassword().length() > 20) {
			throw new InvalidUserDataException("A senha deve ter entre 6 e 20 caracteres");
		}

		if (!r.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%¨_\\-]).*$")) {
			throw new InvalidUserDataException(
					"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -");
		}
	}

	public void setMessageDefaultForPropertiesJSON(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
	}
}
