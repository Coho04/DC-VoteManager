package de.goldendeveloper.votemanager.errors;

import io.github.coho04.mysql.errors.ExceptionHandler;
import io.sentry.Sentry;

public class CustomExceptionHandler extends ExceptionHandler {

    @Override
    public void callException(Exception exception) {
        Sentry.captureException(exception);
        exception.printStackTrace();
    }
}
