package pl.lotto.infrastructure.numberreceiver.controller;

import java.util.List;

public record NumberReceiverRequestDto(List<Integer> inputNumbers) {
}
