package com.quazzom.mastermind.businessrules;

import java.time.LocalDateTime;

import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

public class UserBusinessRule extends User {

	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

	public UserBusinessRule(String name, String email, String nickname, Integer age, String password) {
		this(0L, name, email, nickname, age, password, 0, LocalDateTime.now());
	}

	public UserBusinessRule(Long id, String name, String email, String nickname, Integer age,
			String password, Integer loginAttempts, LocalDateTime createdAt) {
		this(id, name, email, nickname, age, password, loginAttempts, createdAt,
				new MessageDefaultForPropertiesJSON());
	}

	public UserBusinessRule(Long id, String name, String email, String nickname, Integer age,
			String password, Integer loginAttempts,
			LocalDateTime createdAt, MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {

		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;

		setId(id);
		setName(name);
		setEmail(email);
		setNickname(nickname);
		setAge(age);
		setPassword(password);
		setLoginAttempts(loginAttempts);
		setCreatedAt(createdAt);

	}

	// Getters e Setters

	@Override
	public void setName(String name) {

		if (name == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("name"));
		}

		if (!name.matches("^[A-Za-z ]{1,60}$")) {
			throw new RequestInvalidPropertyValueException(
					"O nome deve conter apenas letras e espaços, com no máximo 60 caracteres");
		}

		super.setName(name);
	}

	@Override
	public void setEmail(String email) {
		if (email == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("email"));
		}

		email = email.trim();

		if (email.isEmpty()) {
			throw new RequestInvalidPropertyValueException("A propriedade 'email' não pode ser vazia.");
		}

		if (email.length() > 50) {
			throw new RequestInvalidPropertyValueException("O email deve ter no máximo 50 caracteres");
		}

		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{1,}$")) {
			throw new RequestInvalidPropertyValueException(
					"O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e outro domínio)");
		}

		super.setEmail(email);
	}

	@Override
	public void setNickname(String nickname) {

		if (nickname == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("nickname"));
		}

		if (!nickname.matches("^[a-z][a-z0-9]{3,19}$")) {
			throw new RequestInvalidPropertyValueException(
					"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres");
		}

		super.setNickname(nickname);
	}

	@Override
	public void setAge(Integer age) {
		if (age == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("age"));
		}

		if (age < 1 || age > 120) {
			throw new RequestInvalidPropertyValueException("A idade deve ser um número entre 1 e 120");
		}
		super.setAge(age);
	}

	@Override
	public void setPassword(String password) {
		checkPassword(password, false);
		super.setPassword(password);
	}

	public void setPassword(String password, boolean isNew){
		checkPassword(password, isNew);
		super.setPassword(password);
	}

	private void checkPassword(String password, boolean isNew){
		String prefix = isNew ? "nova " : "";

		if (password == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("password"));
		}

		if (password.length() < 6 || password.length() > 20) {
			throw new RequestInvalidPropertyValueException(
					String.format("A %ssenha deve ter entre 6 e 20 caracteres", prefix));
		}

		if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%¨_\\-]).*$")) {
			throw new RequestInvalidPropertyValueException(String.format(
				"A %ssenha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ %% ¨ _ -", prefix));
		}
	}


	public void setMessageDefaultForPropertiesJSON(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
	}
}
