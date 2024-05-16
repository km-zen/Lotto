package pl.lotto.domain.numbergenerator;

import pl.lotto.domain.numbergenerator.dto.SixRandomNumbersDto;

import java.util.Set;

public interface RandomNumberGenerable {

    SixRandomNumbersDto generateSixRandomNumbers(int count, int lowerBand, int upperBand);
}
