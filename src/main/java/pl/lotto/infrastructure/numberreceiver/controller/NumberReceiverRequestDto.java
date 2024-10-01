package pl.lotto.infrastructure.numberreceiver.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record NumberReceiverRequestDto(
        @NotNull(message = "inputNumbers must not be null")
        @NotEmpty(message = "inputNumbers must not be empty")
        List<Integer> inputNumbers) {

//        @NotNull(message = "{inputNumbers.not.null}")
//        @NotEmpty(message = "{inputNumbers.not.empty}")
}
