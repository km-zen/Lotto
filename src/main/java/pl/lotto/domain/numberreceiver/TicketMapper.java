package pl.lotto.domain.numberreceiver;

import pl.lotto.domain.numberreceiver.dto.TicketDto;

import java.util.stream.Collectors;

public class TicketMapper {

    static TicketDto mapFromTicket(Ticket ticket){
        return TicketDto.builder()
                .ticketId(ticket.ticketId())
                .drawDate(ticket.drawDate())
                .numbersFromUser(ticket.numbersFromUser())
                .build();
    }
}
