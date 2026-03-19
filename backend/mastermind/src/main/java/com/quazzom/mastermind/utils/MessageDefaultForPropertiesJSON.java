package com.quazzom.mastermind.utils;

public class MessageDefaultForPropertiesJSON {

	public String createMessageForPropertyThatDoesNotExist(String propertyName) {
		return "A propriedade '" + propertyName + "' deve existir.";
	}

}
