package pl.lotto.domain.numberreceiver;

import lombok.AllArgsConstructor;
import pl.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import pl.lotto.domain.numberreceiver.dto.TicketDto;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
class NumberReceiverFacade {
    private final NumberValidator validator;
    private final NumberReceiverRepository numberReceiverRepository;
    private final Clock clock;


    public InputNumberResultDto inputNumbers(Set<Integer> numbers) {
        boolean areAllNumbersInRange = validator.areAllNumbersInRange(numbers);
        if (areAllNumbersInRange) {
            String ticketId = UUID.randomUUID().toString();
            LocalDateTime drawDate = LocalDateTime.now(clock);
            Ticket savedTicket = numberReceiverRepository.save(new Ticket(ticketId,drawDate,numbers));
            return InputNumberResultDto.builder()
                    .drawDate(savedTicket.drawDate())
                    .ticketId(savedTicket.ticketId())
                    .numbersFromUser(numbers)
                    .message("success")
                    .build();
        } else {
            return InputNumberResultDto.builder()
                    .message("failed").build();
        }
    }

    public List<TicketDto> userNumbers(LocalDateTime date){
        final List<Ticket> allTicketsByDrawDate = numberReceiverRepository.findAllTicketsByDrawDate(date);
        return allTicketsByDrawDate.stream()
                .map(TicketMapper::mapFromTicket)
                .collect(Collectors.toList());

    }


}
