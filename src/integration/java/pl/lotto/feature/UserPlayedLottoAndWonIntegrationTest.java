package pl.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.numbergenerator.WinningNumbersNotFoundException;
import pl.lotto.domain.numberreceiver.dto.NumberReceiverResponseDto;
import pl.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import pl.lotto.domain.resultchecker.PlayerResultNotFoundException;
import pl.lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.domain.resultchecker.dto.ResultDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


class UserPlayedLottoAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    public WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Autowired
    public ResultCheckerFacade resultCheckerFacade;

    @Test
    public void should_user_win_and_system_generate_winners() throws Exception {
        // given

        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withHeader("Content-Type", "application/json").withBody("""
                [1, 2, 3, 4, 5, 6, 82, 82, 83, 83, 86, 57, 10, 81, 53, 93, 50, 54, 31, 88, 15, 43, 79, 32, 43]
                          """.trim())));

//    step 1: external service returns 6 random numbers (1,2,3,4,5,6)
//    step 2: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at
//          16-11-2022 10:00 and system returned OK(200) with message:
//          “success” and Ticket (DrawDate:19.11.2022 12:00 (Saturday), TicketId: sampleTicketId)

        // given
//        LocalDateTime drawDate = LocalDateTime.of(2024, 5, 25, 12, 0, 0);
        LocalDateTime drawDate = LocalDateTime.of(2022, 11, 19, 12, 0, 0);
        // when
        await().atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofSeconds(1)).until(() -> {
                    try {
                        return !winningNumbersGeneratorFacade.retrieveWinningNumberByDate(drawDate).getWinningNumbers().isEmpty();
                    } catch (WinningNumbersNotFoundException e) {
                        return false;
                    }
                }

        );
        // then
//    step 3: system fetched winning numbers for draw date: 19.11.2022 12:00

        // given
        // when
        ResultActions resultActions = mockMvc.perform(post("/inputNumbers")
                .content("""
                        {
                        "inputNumbers": [1,2,3,4,5,6]
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON));
        // then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        NumberReceiverResponseDto numberReceiverResponseDto = objectMapper.readValue(json, NumberReceiverResponseDto.class);
        String ticketId = numberReceiverResponseDto.ticketDto().hash();
        assertAll(
                () -> assertThat(numberReceiverResponseDto.ticketDto().drawDate()).isEqualTo(drawDate),
                () -> assertThat(numberReceiverResponseDto.ticketDto().hash()).isNotNull(),
                () -> assertThat(numberReceiverResponseDto.message()).isEqualTo("SUCCESS")
        );
//    step 4: 3 days, 2hrs and 1 minute passed, and it is 1 minute after the draw date (19.11.2022 12:01)
        // given
        // when
        ResultActions performGetResultsWithNotExistingId = mockMvc.perform(get("/results/notExistingId"));
        // then
        performGetResultsWithNotExistingId.andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                        "message": "Not found for id: notExistingId",
                        "status": "NOT_FOUND"
                        }
                        """.trim()
                ));

//    step 5: 3 days and 55 minutes passed, and it is 5 minute before draw (19.11.2022 11:55)
// given & when & then
        clock.plusDaysAndMinutes(3,55);

//    step 6: 3 hours passed, and it is 1 minute after announcement time (19.11.2022 15:01)

        await().atMost(20, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> {
                            try {
                                ResultDto result = resultCheckerFacade.findTicketById(ticketId);
                                return !result.numbers().isEmpty();
                            } catch (PlayerResultNotFoundException exception) {
                                return false;
                            }
                        }
                );
//    step 7: 6 minutes passed and it is 1 minute after the draw (19.11.2022 12:01)
        clock.plusMinutes(6);
//    step 8: user made GET /results/sampleTicketId and system returned 200 (OK)

        // given && when
        ResultActions performGetMethod = mockMvc.perform(get("/results/" + ticketId));

        // then
        MvcResult mvcResultGetMethod = performGetMethod.andExpect(status().isOk()).andReturn();
        String jsonGetMethod = mvcResultGetMethod.getResponse().getContentAsString();
        ResultAnnouncerResponseDto finalResult = objectMapper.readValue(jsonGetMethod, ResultAnnouncerResponseDto.class);
        assertAll(
                () -> assertThat(finalResult.message()).isEqualTo("Congratulations, you won!"),
                () -> assertThat(finalResult.responseDto().hash()).isEqualTo(ticketId),
                () -> assertThat(finalResult.responseDto().hitNumbers()).hasSize(6));

    }
}
