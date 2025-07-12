# 🎣 Catchy 🌣️

> A lightweight Java utility to replace repetitive try/catch blocks with clean, fluent, retryable error handling — with optional logging and recovery.

---

## 💡 Why Catchy?

In Java, handling exceptions can be noisy. `Catchy` brings clarity.

* ✅ Minimal boilerplate
* 🔁 Built-in retry with optional backoff
* 🔥 Smart SLF4J logging
* ♻️ Transform and recover from exceptions
* ✨ Chainable, expressive, readable

### How it works

`Catchy` is powered by a fluent interface (`TryWrapper`) and a result container (`Result<T>`), making exception handling composable, type-safe, and flexible.

* You wrap risky code inside `TryWrapper.tryCatch(() -> ...)`, and it returns a `Result<T>`.
* That `Result<T>` will either hold the **value** or the **exception**.
* You can then **chain** behavior like `.recover()`, `.logIfFailure()`, `.onSuccess()` or `.onFailure()`.
* Logging uses SLF4J and will smartly detect the severity level based on the type of exception.
* Recovery is configurable with fallbacks from supplier, value, or message.
* Retry logic lets you retry operations with optional exponential backoff and delay.

Behind the scenes, `Catchy` handles retries via a loop, applying delay between attempts and catching exceptions along the way. The final result is always wrapped in a `Result<T>` object so you don’t need to write messy try/catch blocks everywhere.

### ASCII Diagram

```text
       +----------------------------+
       |      Risky Code Block     |
       |   () -> doSomething()     |
       +----------------------------+
                     |
                     v
         +-----------------------+
         |    tryCatch() wraps   |
         +-----------------------+
                     |
         +-----------+-----------+
         |                       |
         v                       v
+------------------+    +------------------+
|  Success Result  |    | Failure Result   |
|  result.value    |    | result.exception |
+------------------+    +------------------+
         |                       |
         v                       v
   .onSuccess(...)         .onFailure(...)
         |                       |
         v                       v
  your success logic     your error logic
         |                       |
         +-----------------------+
                     |
                     v
        Optional: .recover(), .logIfFailure()
```

---

## 📦 Installation

If you're using **JitPack**, add this to your `pom.xml`:

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

Or you can add the source directly to your project if you prefer.

[![](https://jitpack.io/v/justme8code/catchy.svg)](https://jitpack.io/#justme8code/catchy)

---

## 🚀 Usage

### Basic Try-Catch

```java
Result<String> result = TryWrapper.tryCatch(() -> riskyCode());

result.onSuccess(val -> System.out.println("Yay: " + val))
      .onFailure(err -> System.err.println("Oops: " + err.getMessage()));
```

### Retry with backoff

```java
TryWrapper.tryCatch(() -> connectToService(), 3);
```

### Retry + backoff + delay

```java
TryWrapper.tryCatch(() -> fetch(), null, 5, 100, true);
```

---

## 💠 Recovery

```java
TryWrapper.tryCatch(() -> mightFail())
    .recover(() -> "default value")         // from supplier
    .recoverWithValue("safe fallback")      // static value
    .recoverWithMessage("fallback msg");    // mostly for Strings
```

---

## 📓 Logging (SLF4J)

```java
TryWrapper.tryCatch(() -> riskyCall())
    .logIfFailure(logger)
    .onFailure(err -> System.out.println("Something went wrong"));
```

### Auto log levels:

| Exception Type             | Level   |
| -------------------------- | ------- |
| `NullPointerException`     | `WARN`  |
| `IllegalArgumentException` | `WARN`  |
| `RuntimeException`         | `ERROR` |
| Anything else              | `INFO`  |

Manual control:

```java
result.logWarnIfFailure(logger, "This is bad but recoverable");
result.logInfoIfFailure(logger, "Just so you know...");
result.logIfFailure(logger, "Fully custom msg", false); // always logs as ERROR
```

---

## ✅ Example

```java
Result<String> result = TryWrapper.tryCatch(() -> fetchFromServer())
    .recover(() -> "default-value")
    .logIfFailure(logger)
    .onSuccess(val -> System.out.println("Got: " + val))
    .onFailure(err -> System.err.println("Still failed: " + err.getMessage()));
```

---

## 📣 From the Author

I got tired of noisy `try/catch` blocks in Java. So I built Catchy — a tiny wrapper that:

* Reduces boilerplate
* Adds retry logic with optional backoff
* Supports recovery strategies (`.recover()`)
* Gives you a sweet `.onSuccess()` / `.onFailure()` API
* Logs errors using SLF4J (with auto log levels!)

Think `Try` from FP (functional programming), but practical and designed for real-world Java.

GitHub: [github.com/justme8code/catchy](https://github.com/justme8code/catchy)
JitPack: [jitpack.io/#justme8code/catchy](https://jitpack.io/#justme8code/catchy)

If you’ve ever written the same `try/catch` five times in a day — this one’s for you.

---

## 🔮 Roadmap

* [ ] Custom exception types
* [ ] Async version (not planned but possible)
* [ ] Gradle/Maven deploy
* [ ] `throwIfFailure()` and sealed `Result`

---

## 🧠 Inspiration

Think of `Catchy` like Java’s `Try` from functional programming, but cleaner and closer to how developers *actually* work with exceptions day-to-day.


---

## 📏 Philosophy

`Catchy` is intentionally small — fewer than 500 lines of core logic.

Because great developer tools don’t have to be big.  
They just have to *make you write less code* and *think more clearly*.
