package com.quazzom.mastermind.dto;

import java.util.List;

public record GameGuessRequest(List<Integer> guess) {
}