package pl.lotto.domain.resultchecker;

public class PlayerResultNotFoundException extends RuntimeException{
    PlayerResultNotFoundException(final String message) {
        super(message);
    }
}
