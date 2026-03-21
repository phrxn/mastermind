package com.quazzom.mastermind.businessrules;

import java.util.List;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

@Component
public class GameBusinessRole extends Game {

	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

	public GameBusinessRole() {
		this(new MessageDefaultForPropertiesJSON());
	}

	public GameBusinessRole(MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON) {
		this.messageDefaultForPropertiesJSON = messageDefaultForPropertiesJSON;
	}

	@Override
	public void setLevel(GameLevel level) {

		if (level == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("level"));
		}

		int levelValue = level.ordinal() + 1; // Convert enum to integer (1-based)

		if (levelValue < 1 || levelValue > 4) {
			throw getLevelInvalidExceptionMessage();
		}

		super.setLevel(level);
	}

	public void setLevel(Integer level) {

		if (level == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("level"));
		}
		switch (level) {
			case 1 -> this.setLevel(GameLevel.EASY);
			case 2 -> this.setLevel(GameLevel.NORMAL);
			case 3 -> this.setLevel(GameLevel.HARD);
			case 4 -> this.setLevel(GameLevel.MASTERMIND);
			default -> throw getLevelInvalidExceptionMessage();
		}
	}

	public void setGuess(List<Integer> guess){

		if (guess == null) {
			throw new RequestPropertyNotFoundException(
					messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("guess"));
		}

		if (guess.size() != 4 && guess.size() != 6){
			throw new RequestInvalidPropertyValueException("A quantidade de cores na propriedade 'guess' deve ser 4 ou 6");
		}
	}

	private RequestInvalidPropertyValueException getLevelInvalidExceptionMessage() {
		return new RequestInvalidPropertyValueException("O nível deve ser um valor entre 1 e 4");
	}
}
