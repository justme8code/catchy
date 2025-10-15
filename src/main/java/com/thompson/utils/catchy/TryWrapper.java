package com.thompson.utils.catchy;

import org.slf4j.Logger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class TryWrapper {

    @FunctionalInterface
    public interface TryBlock<T> {
        T run() throws TryBlockException;
    }

    @FunctionalInterface
    public interface VoidTryBlock {
        void run() throws TryBlockException;
    }

    @FunctionalInterface
    public interface ExceptionTransformer {
        Exception transform(Exception original);
    }

    public static <T> Result<T> tryCatch(TryBlock<T> block) {
        return tryCatch(block, null, 0, 0, false);
    }

    public static <T> Result<T> tryCatch(TryBlock<T> block, int retries) {
        return tryCatch(block, null, retries, 0, false);
    }

    public static <T> Result<T> tryCatch(TryBlock<T> block, ExceptionTransformer transformer) {
        return tryCatch(block, transformer, 0, 0, false);
    }

    public static <T> Result<T> tryCatch(TryBlock<T> block, ExceptionTransformer transformer, int retries) {
        return tryCatch(block, transformer, retries, 0, false);
    }

    public static <T> Result<T> tryCatch(TryBlock<T> block, ExceptionTransformer transformer, int retries, long delayMs, boolean useBackoff) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= retries + 1; attempt++) {
            try {
                return Result.success(block.run());
            } catch (Exception e) {
                lastException = e;

                if (attempt <= retries) {
                    long sleepTime = useBackoff ? (long) Math.pow(2, attempt - 1) * delayMs : delayMs;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        Exception finalEx = transformer != null ? transformer.transform(lastException) : lastException;
        return Result.failure(finalEx);
    }

    public static void tryCatchVoid(VoidTryBlock block, ExceptionTransformer transformer, int retries, long delayMs, boolean useBackoff) {
        for (int attempt = 1; attempt <= retries + 1; attempt++) {
            try {
                block.run();
                return;
            } catch (Exception e) {
                if (attempt <= retries) {
                    long sleepTime = useBackoff ? (long) Math.pow(2, attempt - 1) * delayMs : delayMs;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    throw new TryBlockException(transformer != null ? transformer.transform(e) : e);
                }
            }
        }
    }



    public static class Result<T> {
        private final T value;
        private final Exception exception;

        private Result(T value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public static <T> Result<T> success(T value) {
            return new Result<>(value, null);
        }

        public static <T> Result<T> failure(Exception exception) {
            return new Result<>(null, exception);
        }

        public boolean isSuccess() {
            return exception == null;
        }

        public T getValue() {
            return value;
        }

        public Exception getException() {
            return exception;
        }

        public void ifFailure(Consumer<Exception> handler) {
            if (!isSuccess()) {
                handler.accept(exception);
            }
        }

        public Result<T> onSuccess(Consumer<T> consumer) {
            if (isSuccess()) {
                consumer.accept(value);
            }
            return this;
        }

        public Result<T> onFailure(Consumer<Exception> consumer) {
            if (!isSuccess()) {
                consumer.accept(exception);
            }
            return this;
        }

        public <R> Result<R> map(Function<T, R> mapper) {
            if (isSuccess()) {
                try {
                    return Result.success(mapper.apply(value));
                } catch (Exception e) {
                    return Result.failure(e);
                }
            } else {
                return Result.failure(exception);
            }
        }

        public Result<T> recover(Supplier<T> fallbackSupplier) {
            if (isSuccess()) {
                return this;
            } else {
                try {
                    return Result.success(fallbackSupplier.get());
                } catch (Exception e) {
                    return Result.failure(e);
                }
            }
        }

        public Result<T> recoverWithValue(T fallbackValue) {
            if (isSuccess()) {
                return this;
            } else {
                return Result.success(fallbackValue);
            }
        }

        public Result<T> recoverWithMessage(String fallbackMessage) {
            if (isSuccess()) {
                return this;
            } else {
                return Result.success((T) fallbackMessage);
            }
        }

        public Result<T> logIfFailure(Logger logger) {
            return logIfFailure(logger, "[TryWrapper] Exception occurred:", true);
        }

        public Result<T> logIfFailure(Logger logger, String customMessage) {
            return logIfFailure(logger, customMessage, true);
        }

        public Result<T> logWarnIfFailure(Logger logger, String customMessage) {
            if (!isSuccess()) {
                logger.warn(customMessage, exception);
            }
            return this;
        }

        public Result<T> logInfoIfFailure(Logger logger, String customMessage) {
            if (!isSuccess()) {
                logger.info(customMessage, exception);
            }
            return this;
        }

        public Result<T> logIfFailure(Logger logger, String customMessage, boolean autoLevel) {
            if (!isSuccess()) {
                if (autoLevel) {
                    if (exception instanceof NullPointerException || exception instanceof IllegalArgumentException) {
                        logger.warn("[WARN] {} " ,customMessage, exception);
                    } else if (exception instanceof RuntimeException) {
                        logger.error("[RUNTIME] {} ", customMessage, exception);
                    } else {
                        logger.info("[INFO] {} ", customMessage, exception);
                    }
                } else {
                    logger.error(customMessage, exception);
                }
            }
            return this;
        }
    }



    // NEW: tryCatchFunction() — input -> output functional form
    public static <I, O> Result<O> tryCatchFunction(I input, Function<I, O> function) {
        return tryCatchFunction(input, function, null, 0, 0, false);
    }

    public static <I, O> Result<O> tryCatchFunction(I input, Function<I, O> function, int retries) {
        return tryCatchFunction(input, function, null, retries, 0, false);
    }

    public static <I, O> Result<O> tryCatchFunction(I input, Function<I, O> function,
                                                    ExceptionTransformer transformer,
                                                    int retries, long delayMs, boolean useBackoff) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= retries + 1; attempt++) {
            try {
                return Result.success(function.apply(input));
            } catch (Exception e) {
                lastException = e;

                if (attempt <= retries) {
                    long sleepTime = useBackoff ? (long) Math.pow(2, attempt - 1) * delayMs : delayMs;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        Exception finalEx = transformer != null ? transformer.transform(lastException) : lastException;
        return Result.failure(finalEx);
    }

}
