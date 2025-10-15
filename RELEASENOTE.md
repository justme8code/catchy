# 🎣 Catchy Release Notes

## v1.1.3 - Resource Management Without `try`

**Released:** [Date]

### 🌟 What's New

#### `Catchy.autoClose()` - Zero-Boilerplate Resource Management

Say goodbye to `try-with-resources` blocks! The new `Catchy` class introduces `autoClose()` for managing resources like `InputStream`, `Reader`, or any `AutoCloseable` without writing a single `try` keyword.

**Before:**
```java
try (var input = Files.newInputStream(path)) {
    return new String(input.readAllBytes());
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

**After:**
```java
TryWrapper.tryCatch(() ->
    Catchy.autoClose(Files.newInputStream(path), input -> 
        new String(input.readAllBytes())
    )
)
.onSuccess(content -> LOGGER.info("File loaded"))
.onFailure(err -> LOGGER.error("Load failed", err));
```

### 💡 Design Evolution

Introduced the `Catchy` class as the library's central API entry point:
- More expressive and memorable than `TryWrapper`
- Aligns with the library's philosophy of *removing* `try`, not wrapping it
- Foundation for future utilities and features

### 🔧 API Additions

- `Catchy.autoClose(R resource, ThrowingFunction<R, T> function)` - Automatic resource cleanup with functional syntax

---

## v1.1.2 - Full Checked Exception Support

**Released:** [Date]

### 🎯 The Big Change

**Goal achieved:** Stop writing `try-catch` blocks. Forever.

Catchy now fully supports checked exceptions (`IOException`, `SQLException`, etc.) through throwing functional interfaces. No more boilerplate for file I/O, database operations, or network calls.

### ⚡ What's New

#### Throwing Functional Interfaces

```java
TryWrapper.tryCatch(() -> {
    var input = Files.newInputStream(path);
    return new String(input.readAllBytes());
})
.onSuccess(content -> LOGGER.info("Loaded successfully"))
.onFailure(err -> LOGGER.error("Read failed", err))
.recover(() -> "default");
```

**No `try`. No `catch`. Just clean, fluent code.**

### 🔧 API Changes

- Introduced `ThrowingSupplier<T>` interface
- Introduced `ThrowingFunction<I, O>` interface
- Updated all `tryCatch*` methods to accept throwing functional interfaces
- Full support for checked exceptions across the entire API

### ✨ Benefits

- Zero boilerplate for checked exceptions
- Works with file I/O, JDBC, reflection, and more
- Maintains type safety and exception information
- Seamless integration with existing Catchy features

---

## v1.1.1 - External Data Support

**Released:** [Date]

### 🧰 New Features

#### `tryCatchInput` - Context-Aware Exception Handling

Safely execute code that consumes and mutates external data:

```java
TryWrapper.tryCatchInput(fileResults, list -> {
    String hash = computeHash(cleanedFile);
    list.add(new FileResult(hash, originalName, cleanedFile));
});
```

**With retries and backoff:**
```java
TryWrapper.tryCatchInput(
    fileResults,
    list -> list.add(fetchRemoteData()),
    null,     // ExceptionTransformer
    3,        // retries
    500,      // delayMs
    true      // useBackoff
);
```

#### `tryCatchFunction` - Functional Input/Output Transformations

Transform data with automatic exception handling:

```java
var result = TryWrapper.tryCatchFunction(cleanedFile, file -> {
    String hash = computeHash(file);
    return new FileResult(hash, originalName, file);
});

result.onSuccess(fileResults::add);
```

### 🔧 API Additions

- `tryCatchInput(I input, Consumer<I> consumer)` - Execute code that mutates external data
- `tryCatchFunction(I input, Function<I, O> function)` - Transform input to output with exception handling
- Full retry, delay, and backoff support for both methods

### ✨ Benefits

- Eliminates "variable must be final or effectively final" lambda issues
- Cleaner handling of context-dependent logic
- Fully integrated with retries and exception transformation
- Preserves functional, chainable `Result<T>` API

---

## v1.0.0 - Initial Release

**Released:** [Date]

### 🎉 Welcome to Catchy!

A lightweight Java utility that replaces repetitive try/catch blocks with clean, fluent, retryable error handling.

### ✨ Core Features

#### Fluent Exception Handling
```java
Result<String> result = TryWrapper.tryCatch(() -> riskyCode());

result.onSuccess(val -> System.out.println("Success: " + val))
      .onFailure(err -> System.err.println("Error: " + err.getMessage()));
```

#### Built-in Retry Logic
```java
// Simple retry
TryWrapper.tryCatch(() -> connectToService(), 3);

// With exponential backoff
TryWrapper.tryCatch(() -> fetch(), null, 5, 100, true);
```

#### Recovery Strategies
```java
TryWrapper.tryCatch(() -> mightFail())
    .recover(() -> "default value")         // from supplier
    .recoverWithValue("safe fallback")      // static value
    .recoverWithMessage("fallback message"); // for strings
```

#### Value Transformation
```java
TryWrapper.tryCatch(() -> 5)
    .map(val -> val * 2)
    .onSuccess(System.out::println); // prints: 10
```

#### Smart SLF4J Logging
```java
TryWrapper.tryCatch(() -> riskyCall())
    .logIfFailure(logger)
    .onFailure(err -> System.out.println("Handled gracefully"));
```

**Auto log levels:**
- `NullPointerException` → WARN
- `IllegalArgumentException` → WARN
- `RuntimeException` → ERROR
- Everything else → INFO

### 🔧 API Overview

**Core Methods:**
- `tryCatch(Supplier<T> supplier)` - Basic exception wrapping
- `tryCatch(Supplier<T> supplier, int maxRetries)` - With retry
- `tryCatch(Supplier<T> supplier, ExceptionTransformer transformer, int maxRetries, long delayMs, boolean useBackoff)` - Full configuration

**Result Methods:**
- `.onSuccess(Consumer<T>)` - Handle success
- `.onFailure(Consumer<Exception>)` - Handle failure
- `.recover(Supplier<T>)` - Provide fallback
- `.map(Function<T, R>)` - Transform value
- `.logIfFailure(Logger)` - Log errors
- `.get()` - Unwrap value (may throw)
- `.orElse(T)` - Get value or default

### 📦 Installation

Available via JitPack:

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
    <version>v1.0.0</version>
  </dependency>
</dependencies>
```

### 🎯 Design Philosophy

**Small by design** - Less than 500 lines of core logic

Great developer tools don't have to be big. They just have to make you write less code and think more clearly.

---

## Migration Guide

### From v1.1.2 to v1.1.3
- No breaking changes
- Consider using `Catchy.autoClose()` for resource management
- `TryWrapper` remains fully supported

### From v1.1.1 to v1.1.2
- No breaking changes
- All lambda expressions now support checked exceptions automatically
- Remove manual `try-catch` wrapping inside lambdas

### From v1.0.0 to v1.1.1
- No breaking changes
- New methods available: `tryCatchInput()` and `tryCatchFunction()`
- Existing code continues to work unchanged

---

## What's Next?

### Planned Features
- Custom exception type filtering
- Async/reactive version exploration
- Enhanced retry policies
- `throwIfFailure()` and sealed `Result` types
- Performance optimizations

---

## Credits

Created with 🐟 by [@justme8code](https://github.com/justme8code)

**Links:**
- GitHub: [github.com/justme8code/catchy](https://github.com/justme8code/catchy)
- JitPack: [jitpack.io/#justme8code/catchy](https://jitpack.io/#justme8code/catchy)