package pl.lotto.domain.resultchecker;

import lombok.AllArgsConstructor;
import pl.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.Ticket;
import pl.lotto.domain.numberreceiver.dto.TicketDto;
import pl.lotto.domain.resultchecker.dto.PlayersDto;
import pl.lotto.domain.resultchecker.dto.ResultDto;

import java.util.List;
import java.util.Set;

import static pl.lotto.domain.resultchecker.ResultCheckerMapper.mapPlayersToResults;

@AllArgsConstructor
public class ResultCheckerFacade {

    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    NumberReceiverFacade numberReceiverFacade;
    PlayerRepository playerRepository;
    WinnersRetriever winnerGenerator;


    public PlayersDto generateResults() {
        List<TicketDto> allTicketsByDate = numberReceiverFacade.retrieveAllTicketsByNextDrawDate();
        List<Ticket> tickets = ResultCheckerMapper.mapFromTicketDto(allTicketsByDate);
        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.generateWinningNumbers();
        Set<Integer> winningNumbers = winningNumbersDto.getWinningNumbers();
        if (winningNumbers == null || winningNumbers.isEmpty()) {
            return PlayersDto.builder()
                    .message("Winners failed to retrieve")
                    .build();
        }
        List<Player> players = winnerGenerator.retrieveWinners(tickets, winningNumbers);
        playerRepository.saveAll(players);
        return PlayersDto.builder()
                .results(mapPlayersToResults(players))
                .message("Winners succeeded to retrieve")
                .build();
    }

    public ResultDto findTicketById(String hash) {
        Player player = playerRepository.findById(hash).orElseThrow(() -> new PlayerResultNotFoundException("Not found for id: " + hash));
        return ResultDto.builder()
                .hash(hash)
                .numbers(player.numbers())
                .hitNumbers(player.hitNumbers())
                .drawDate(player.drawDate())
                .isWinner(player.isWinner())
                .build();
    }
}
