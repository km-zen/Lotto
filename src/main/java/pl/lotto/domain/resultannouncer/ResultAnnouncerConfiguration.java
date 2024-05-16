package pl.lotto.domain.resultannouncer;

import pl.lotto.domain.resultchecker.ResultCheckerFacade;

import java.time.Clock;

class ResultAnnouncerConfiguration {


    ResultAnnouncerFacade createForTest(ResultCheckerFacade resultCheckerFacade, ResponseRepository responseRepository, Clock clock) {
        return new ResultAnnouncerFacade(resultCheckerFacade, responseRepository, clock);
    }
}
