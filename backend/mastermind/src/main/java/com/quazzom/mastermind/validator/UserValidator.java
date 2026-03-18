package com.quazzom.mastermind.validator;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.exception.InvalidUserDataException;

@Component
public class UserValidator {

	public void validateRegister(RegisterRequest r) {

		isNameValid(r);
		isNicknameValid(r);
		isAgeValid(r);
		isPasswordValid(r);

	}

	public void isNameValid(RegisterRequest r) {
		if (r.getName() == null) {
			throw new InvalidUserDataException("A propriedade 'nome' deve existir.");
		}

		if (!r.getName().matches("^[A-Za-z ]{1,60}$")) {
			throw new InvalidUserDataException(
					"O nome deve conter apenas letras e espaços, com no máximo 60 caracteres");
		}
	}

	public void isNicknameValid(RegisterRequest r) {
		if (r.getNickname() == null) {
			throw new InvalidUserDataException("A propriedade 'nickname' deve existir.");
		}

		if (!r.getNickname().matches("^[a-z][a-z]{0,19}$")) {
			throw new InvalidUserDataException(
					"O nickname deve conter apenas letras minúsculas, com no máximo 20 caracteres");
		}
	}

	public void isAgeValid(RegisterRequest r){
		if (r.getAge() == null){
			throw new InvalidUserDataException("A propriedade 'age' deve existir.");
		}

		if (r.getAge() < 1 || r.getAge() > 120) {
			throw new InvalidUserDataException("A idade deve ser um número entre 1 e 120");
		}
	}

	public void isPasswordValid(RegisterRequest r){
		if (r.getPassword() == null){
			throw new InvalidUserDataException("A propriedade 'password' deve existir.");
		}

		if (r.getPassword().length() < 6 || r.getPassword().length() > 16) {
			throw new InvalidUserDataException("A senha deve ter entre 6 e 16 caracteres");
		}

		if (!r.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%¨_\\-]).*$")) {
			throw new InvalidUserDataException(
					"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -");
		}
	}
}