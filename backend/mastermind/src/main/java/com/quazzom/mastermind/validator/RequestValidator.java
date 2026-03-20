package com.quazzom.mastermind.validator;

public interface RequestValidator<T> {

	void validateRequestBody(T request);

}
