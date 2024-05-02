package pl.lotto.domain.numberreceiver;

import org.junit.jupiter.api.Test;
import pl.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import pl.lotto.domain.numberreceiver.dto.TicketDto;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NumberReceiverFacadeTest {

    NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
            new NumberValidator(),
            new InMemoryNumberReceiverRepositoryTestImpl(),
    Clock.fixed(LocalDateTime.of(2022,12,17,12,0,0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
    @Test
    public void should_return_success_when_user_gave_six_numbers(){
        // given
        final Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        // when
        InputNumberResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        // then
        assertThat(result.message()).isEqualTo("success");
    }

    @Test
    public void should_return_failed_when_user_gave_less_than_six_numbers(){
        // given
        final Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5);
        // when
        InputNumberResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        // then
        assertThat(result.message()).isEqualTo("failed");
    }

    @Test
    public void should_return_failed_when_user_gave_more_than_six_numbers(){
        // given
        final Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6,7);
        // when
        InputNumberResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        // then
        assertThat(result.message()).isEqualTo("failed");
    }

    @Test
    public void should_return_failed_when_user_gave_at_least_one_number_out_of_range_1_to_99(){

        // given
        final Set<Integer> numbersFromUser = Set.of(1, 2000, 3, 4, 5,6);
        // when
        InputNumberResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        // then
        assertThat(result.message()).isEqualTo("failed");
    }

    @Test
    public void should_return_save_to_database_when_user_gave_six_numbers(){
        // given
        final Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        InputNumberResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        LocalDateTime drawDate = LocalDateTime.of(2022,12,17,13,0,0);
        // when
        List<TicketDto> ticketDtos = numberReceiverFacade.userNumbers(drawDate);
        // then
        assertThat(ticketDtos).contains(
                TicketDto.builder()
                        .drawDate(drawDate)
                        .ticketId(result.ticketId())
                        .numbersFromUser(result.numbersFromUser())
                        .build()
        );
    }

}