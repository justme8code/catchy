# 🎣 Catchy

> Exception handling in Java, but make it clean.

[![JitPack](https://jitpack.io/v/justme8code/catchy.svg)](https://jitpack.io/#justme8code/catchy)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**Catchy** is a lightweight Java utility that eliminates repetitive `try-catch` blocks with fluent, retryable error handling — complete with smart logging, recovery strategies, and automatic resource management.

Stop writing boilerplate. Start catching exceptions gracefully.

---

## 💡 Why Catchy?

Java's exception handling is powerful but verbose. Catchy brings clarity without sacrificing control.

**Before Catchy:**
```java
try (var input = Files.newInputStream(path)) {
    String content = new String(input.readAllBytes());
    return processContent(content);
} catch (IOException e) {
    LOGGER.error("Failed to read file", e);
    return "default";
}
```

**After Catchy:**
```java
TryWrapper.tryCatch(() ->
    Catchy.autoClose(Files.newInputStream(path), input ->
        processContent(new String(input.readAllBytes()))
    )
)
.logIfFailure(LOGGER)
.recover(() -> "default")
.get();
```

### What You Get

- 🧹 **Zero boilerplate** - No more `try-catch` noise
- 🔁 **Smart retries** - Built-in retry with exponential backoff
- 🔥 **Auto logging** - SLF4J integration with intelligent log levels
- ♻️ **Recovery modes** - Transform and recover from failures elegantly
- 🎯 **Type-safe** - Full generic support with `Result<T>` container
- 🧰 **Resource-safe** - Automatic cleanup with `autoClose()`
- ✨ **Fluent API** - Chainable, expressive, readable

---

## 📦 Installation

### Maven (via JitPack)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.justme8code</groupId>
    <artifactId>catchy</artifactId>
    <version>v1.1.3</version>
  </dependency>
</dependencies>
```

### Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.justme8code:catchy:v1.1.3'
}
```

---

## 🚀 Quick Start

### Basic Exception Handling

```java
Result<String> result = TryWrapper.tryCatch(() -> riskyOperation());

result
    .onSuccess(val -> System.out.println("Success: " + val))
    .onFailure(err -> System.err.println("Error: " + err.getMessage()));
```

### With Retry

```java
// Retry up to 3 times
TryWrapper.tryCatch(() -> connectToDatabase(), 3);

// Retry with exponential backoff
TryWrapper.tryCatch(
    () -> fetchFromAPI(),
    null,   // exception transformer
    5,      // max retries
    100,    // initial delay (ms)
    true    // use exponential backoff
);
```

### Recovery Strategies

```java
String result = TryWrapper.tryCatch(() -> fetchUserName())
    .recover(() -> "Guest")                    // supplier
    .recoverWithValue("Anonymous")             // static value
    .recoverWithMessage("Unknown User")        // message-based
    .get();
```

### Transform Values Safely

```java
TryWrapper.tryCatch(() -> "42")
    .map(Integer::parseInt)
    .map(n -> n * 2)
    .onSuccess(n -> System.out.println("Result: " + n)) // 84
    .onFailure(err -> System.err.println("Parse failed"));
```

---

## 🌟 Key Features

### 1. Resource Management (v1.1.3)

Automatically close resources without `try-with-resources`:

```java
TryWrapper.tryCatch(() ->
    Catchy.autoClose(new FileInputStream("data.txt"), input ->
        new String(input.readAllBytes())
    )
)
.onSuccess(content -> LOGGER.info("Loaded: {}", content))
.onFailure(err -> LOGGER.error("Failed to load", err));
```

Works with any `AutoCloseable` resource:
- `InputStream`, `OutputStream`
- `Reader`, `Writer`
- Database connections
- Custom resources

### 2. Checked Exception Support (v1.1.2)

Handle checked exceptions without wrapping in `try-catch`:

```java
TryWrapper.tryCatch(() -> {
    // Checked exceptions work seamlessly
    return Files.readString(Path.of("config.json"));
})
.map(json -> parseJson(json))
.onFailure(err -> LOGGER.error("Config load failed", err))
.recover(() -> "{}");
```

### 3. External Data Operations (v1.1.1)

#### Mutate External Objects

```java
List<FileResult> results = new ArrayList<>();

TryWrapper.tryCatchInput(results, list -> {
    String hash = computeHash(file);
    list.add(new FileResult(hash, fileName, file));
});
```

#### Transform Input to Output

```java
var result = TryWrapper.tryCatchFunction(
    userRequest,
    this::processRequest
);

result.onSuccess(response -> sendResponse(response));
```

### 4. Smart Logging

Automatic SLF4J integration with context-aware log levels:

```java
TryWrapper.tryCatch(() -> riskyOperation())
    .logIfFailure(LOGGER);
```

**Auto log levels:**

| Exception Type             | Log Level |
|---------------------------|-----------|
| `NullPointerException`     | WARN      |
| `IllegalArgumentException` | WARN      |
| `RuntimeException`         | ERROR     |
| Other exceptions           | INFO      |

**Manual control:**

```java
result.logWarnIfFailure(LOGGER, "Recoverable error occurred");
result.logInfoIfFailure(LOGGER, "Minor issue detected");
result.logIfFailure(LOGGER, "Critical failure", false); // force ERROR
```

---

## 🧩 API Reference

### Core Methods

#### `tryCatch()`

```java
// Basic usage
Result<T> tryCatch(ThrowingSupplier<T> supplier)

// With retries
Result<T> tryCatch(ThrowingSupplier<T> supplier, int maxRetries)

// Full configuration
Result<T> tryCatch(
    ThrowingSupplier<T> supplier,
    ExceptionTransformer transformer,
    int maxRetries,
    long delayMs,
    boolean useBackoff
)
```

#### `tryCatchInput()`

```java
// Mutate external data
Result<Void> tryCatchInput(I input, Consumer<I> consumer)

// With retries
Result<Void> tryCatchInput(
    I input,
    Consumer<I> consumer,
    ExceptionTransformer transformer,
    int maxRetries,
    long delayMs,
    boolean useBackoff
)
```

#### `tryCatchFunction()`

```java
// Transform input to output
Result<O> tryCatchFunction(I input, Function<I, O> function)

// With retries
Result<O> tryCatchFunction(
    I input,
    Function<I, O> function,
    ExceptionTransformer transformer,
    int maxRetries,
    long delayMs,
    boolean useBackoff
)
```

### Result Methods

#### Success & Failure Handling

```java
result.onSuccess(Consumer<T> consumer)      // Execute on success
result.onFailure(Consumer<Exception> consumer) // Execute on failure
result.get()                                // Unwrap value (throws if failed)
result.orElse(T defaultValue)              // Get value or default
result.isSuccess()                          // Check if successful
result.isFailure()                          // Check if failed
```

#### Recovery

```java
result.recover(Supplier<T> fallback)        // Recover with supplier
result.recoverWithValue(T value)            // Recover with static value
result.recoverWithMessage(String message)   // Recover with message (String only)
```

#### Transformation

```java
result.map(Function<T, R> mapper)           // Transform success value
```

#### Logging

```java
result.logIfFailure(Logger logger)
result.logWarnIfFailure(Logger logger, String message)
result.logInfoIfFailure(Logger logger, String message)
result.logIfFailure(Logger logger, String message, boolean useSmartLevel)
```

### Utility Methods

#### `Catchy.autoClose()`

```java
// Automatic resource management
T autoClose(R resource, ThrowingFunction<R, T> function)
```

---

## 📖 Real-World Examples

### File Processing with Retry

```java
var result = TryWrapper.tryCatch(
    () -> Catchy.autoClose(
        Files.newInputStream(filePath),
        input -> processFileStream(input)
    ),
    null,   // no transformer
    3,      // retry 3 times
    500,    // 500ms initial delay
    true    // exponential backoff
);

result
    .logIfFailure(LOGGER)
    .onSuccess(data -> LOGGER.info("Processed {} records", data.size()))
    .onFailure(err -> notifyAdmin("File processing failed", err));
```

### Database Operation with Recovery

```java
User user = TryWrapper.tryCatch(() -> userRepository.findById(userId))
    .recover(() -> userRepository.findByEmail(email))
    .recoverWithValue(User.guest())
    .logIfFailure(LOGGER)
    .get();
```

### API Call with Transformation

```java
TryWrapper.tryCatch(() -> httpClient.fetch(apiUrl))
    .map(response -> response.body())
    .map(json -> objectMapper.readValue(json, ApiResponse.class))
    .map(ApiResponse::getData)
    .onSuccess(data -> cache.put(cacheKey, data))
    .onFailure(err -> LOGGER.warn("API call failed, using stale cache"))
    .recover(() -> cache.get(cacheKey))
    .get();
```

### Batch Processing with Context

```java
List<ProcessedFile> results = new ArrayList<>();

files.forEach(file -> {
    TryWrapper.tryCatchInput(results, list -> {
        String content = Catchy.autoClose(
            Files.newInputStream(file),
            input -> new String(input.readAllBytes())
        );
        String hash = computeHash(content);
        list.add(new ProcessedFile(file.getName(), hash, content.length()));
    })
    .logIfFailure(LOGGER);
});
```

---

## 🎯 Design Philosophy

### Small by Design

Catchy's core logic is **under 500 lines**. Great tools don't need to be complex — they just need to make you write less code and think more clearly.

### Functional but Practical

Inspired by functional programming's `Try` monad, but designed for everyday Java development. Catchy bridges the gap between FP elegance and Java pragmatism.

### Zero-Compromise Safety

- Type-safe generics throughout
- Automatic resource cleanup
- Exception information preserved
- No hidden behavior or magic

---

## 🔮 Roadmap

- [ ] Custom exception type filters
- [ ] Async/reactive support (exploration phase)
- [ ] Enhanced retry policies (jitter, custom backoff strategies)
- [ ] `throwIfFailure()` for explicit error propagation
- [ ] Sealed `Result` types (when available)
- [ ] Performance benchmarks and optimizations

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🐟 From the Author

I built Catchy because I got tired of writing the same `try-catch` blocks over and over. Java's exception handling is powerful, but it shouldn't require 10 lines of boilerplate every single time.

Catchy is my answer: a tiny library that makes exception handling feel natural, not ceremonial.

If you've ever written the same `try-catch` five times in a day — this one's for you.

**Links:**
- 🐙 GitHub: [github.com/justme8code/catchy](https://github.com/justme8code/catchy)
- 📦 JitPack: [jitpack.io/#justme8code/catchy](https://jitpack.io/#justme8code/catchy)

---

<div align="center">
Made with 🎣 by <a href="https://github.com/justme8code">@justme8code</a>
</div>