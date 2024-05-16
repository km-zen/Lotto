package pl.lotto.infrastructure.numberreceiver.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.dto.NumberReceiverResponseDto;

import java.util.HashSet;
import java.util.Set;

@RestController
@AllArgsConstructor
class InputNumbersRestController {
    private final NumberReceiverFacade numberReceiverFacade;

    @PostMapping("/inputNumbers")
    public ResponseEntity<NumberReceiverResponseDto> inputNumbers(@RequestBody NumberReceiverRequestDto requestDto){
        Set<Integer> distinctNumbers = new HashSet<>(requestDto.inputNumbers());
        NumberReceiverResponseDto numberReceiverResponseDto = numberReceiverFacade.inputNumbers(distinctNumbers);
        return ResponseEntity.ok(numberReceiverResponseDto);
    }
}
