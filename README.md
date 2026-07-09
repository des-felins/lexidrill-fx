# LexiDrill FX

LexiDrill FX is a small desktop demo application that combines **Spring Boot**, **JavaFX**, and **SQLite**.

The app is a simple word trainer. A user can sign up or sign in, add words to their personal study list, and practice those words. The current version focuses on running the app on the regular JVM. Native Image support will be added later.

## What the app does

* Starts as a JavaFX desktop application
* Uses Spring Boot for dependency injection and application wiring
* Uses Spring Data JPA for persistence
* Stores data locally in SQLite
* Supports multiple users
* Keeps a separate word list for each user
* Lets users add study words with:
  * original word or character
  * reading
  * translation
* Lets users choose words to practice
* Shows translations first and reveals the original word on demand
* Shuffles selected words for practice

## Tech stack

* Java 25
* Spring Boot
* Spring Data JPA
* JavaFX
* SQLite
* Maven

## Requirements

You need a JDK that includes JavaFX.

The easiest option is **Liberica JDK Full 25**, because it already contains JavaFX. A regular JDK without JavaFX will not be enough, because the project uses JavaFX classes directly.

You also need:

* Maven, or the included Maven Wrapper
* Git, if you clone the project from a repository

## How to run

Clone the project:

```bash
git clone <repository-url>
cd springfx
```

Run the application with Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application should open as a desktop window.

## Local database

The app stores data in a local SQLite database:

```text
data/words.db
```

The database is created and updated automatically by Hibernate.

During development, if the schema gets into a weird state after entity changes, stop the app and delete:

```text
data/words.db
```

Then run the app again.

## Current user flow

1. Open the app.
2. Sign up with a username, or sign in as an existing user.
3. Open the home screen.
4. Open **Add words**.
5. Add words to your personal study list.
6. Return home using the toolbar.
7. Open **Practice words**.
8. Select the words you want to practice.
9. Start practice.
10. Go through the shuffled words one by one.

## Notes

This is a demo project, not a production-ready language learning app.
There is no password-based authentication, no cloud sync, and no advanced spaced repetition logic. The goal is to demonstrate how a desktop Java application can use JavaFX for the UI, Spring Boot for application structure, and SQLite for local persistence.
