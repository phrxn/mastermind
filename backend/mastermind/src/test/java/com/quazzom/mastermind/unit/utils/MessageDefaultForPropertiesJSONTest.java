package com.quazzom.mastermind.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

class MessageDefaultForPropertiesJSONTest {

    private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

    @BeforeEach
    void setUp() {
        messageDefaultForPropertiesJSON = new MessageDefaultForPropertiesJSON();
    }

    @Test
    void createMessageForPropertyThatDoesNotExistShouldReturnExpectedMessage() {
        String message = messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist("email");

        assertEquals("A propriedade 'email' deve existir.", message);
    }

    @Test
    void createMessageForPropertyThatDoesNotExistShouldReturnExpectedMessageWhenPropertyIsNull() {
        String message = messageDefaultForPropertiesJSON.createMessageForPropertyThatDoesNotExist(null);

        assertEquals("A propriedade 'null' deve existir.", message);
    }
}
