package com.quazzom.mastermind.businessrules;

import java.time.LocalDateTime;

import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

public class UserBusinessRule {

	private Long id;
	private String name;
	private String email;
	private String nickname;
	private Integer age;
	private String password;
	private Integer loginAttempts;
	private Integer bestScoreEasy;
	private Integer bestScoreNormal;
	private Integer bestScoreHard;
	private Integer bestScoreMastermind;
	private LocalDateTime createdAt;

	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

	// Construtores
	//public UserBusinessRule() {
		//this(0L, "", "", "", 0, "", 0, 0, 0, 0, 0, LocalDateTime.now());
	//}

	public UserBusinessRule(String name, String email, String nickname, Integer age, String password) {
		this(0L, name, email, nickname, age, password, 0, 0, 0, 0, 0, LocalDateTime.now());
	}

	public UserBusinessRule(Long id, String name, String email, String nickname, Integer age,
			String password, Integer loginAttempts, Integer bestScoreEasy,
			Integer bestScoreNormal, Integer bestScoreHard, Integer bestScoreMastermind,
			LocalDateTime createdAt) {
		this(id, name, email, nickname, age, password, loginAttempts, bestScoreEasy,
				bestScoreNormal, bestScoreHard, bestScoreMastermind, createdAt,
				new MessageDefaultForPropertiesJSON());
	}

	public UserBusinessRule(Long id, String name, String email, String nickname, Integer age,
			String password, Integer loginAttempts, Integer bestScoreEasy,
			Integer bestScoreNormal, Integer bestScoreHard, Integer bestScoreMastermind,
			LocalDateTime createdAt, MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		setId(id);
		setName(name);
		setEmail(email);
		setNickname(nickname);
		setAge(age);
		setPassword(password);
		setLoginAttempts(loginAttempts);
		setBestScoreEasy(bestScoreEasy);
		setBestScoreNormal(bestScoreNormal);
		setBestScoreHard(bestScoreHard);
		setBestScoreMastermind(bestScoreMastermind);
		setCreatedAt(createdAt);
		setMessageDefaultForPropertiesJSON(messageDefaultForPropertiesJSON);
	}

	// Getters e Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		if (name == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("name"));
		}

		if (!name.matches("^[A-Za-z ]{1,60}$")) {
			throw new RequestInvalidPropertyValueException(
					"O nome deve conter apenas letras e espaços, com no máximo 60 caracteres");
		}

		this.name = name;
	}

	public String getEmail() {
		return email;
	}

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

		if (!email.matches("^[A-Za-z]+@[A-Za-z0-9.-]*[A-Za-z]\\.[A-Za-z][A-Za-z0-9.-]*$")) {
			throw new RequestInvalidPropertyValueException(
					"O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)");
		}

		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {

		if (nickname == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("nickname"));
		}

		if (!nickname.matches("^[a-z][a-z0-9]{3,19}$")) {
			throw new RequestInvalidPropertyValueException(
					"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres");
		}

		this.nickname = nickname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		if (age == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("age"));
		}

		if (age < 1 || age > 120) {
			throw new RequestInvalidPropertyValueException("A idade deve ser um número entre 1 e 120");
		}
		this.age = age;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("password"));
		}

		if (password.length() < 6 || password.length() > 20) {
			throw new RequestInvalidPropertyValueException("A senha deve ter entre 6 e 20 caracteres");
		}

		if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%¨_\\-]).*$")) {
			throw new RequestInvalidPropertyValueException(
					"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -");
		}
		this.password = password;
	}

	public Integer getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Integer loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public Integer getBestScoreEasy() {
		return bestScoreEasy;
	}

	public void setBestScoreEasy(Integer bestScoreEasy) {
		this.bestScoreEasy = bestScoreEasy;
	}

	public Integer getBestScoreNormal() {
		return bestScoreNormal;
	}

	public void setBestScoreNormal(Integer bestScoreNormal) {
		this.bestScoreNormal = bestScoreNormal;
	}

	public Integer getBestScoreHard() {
		return bestScoreHard;
	}

	public void setBestScoreHard(Integer bestScoreHard) {
		this.bestScoreHard = bestScoreHard;
	}

	public Integer getBestScoreMastermind() {
		return bestScoreMastermind;
	}

	public void setBestScoreMastermind(Integer bestScoreMastermind) {
		this.bestScoreMastermind = bestScoreMastermind;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setMessageDefaultForPropertiesJSON(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
	}
}
