package pl.lotto.domain.numbergenerator;

public class WinningNumbersNotFoundException extends RuntimeException {
    WinningNumbersNotFoundException(final String message) {
        super(message);
    }
}
