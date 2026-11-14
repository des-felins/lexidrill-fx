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

## Tests

Use **Liberica JDK Full 25** for both Maven and its test JVM. JavaFX comes from
the JDK, so its version follows the installed Full JDK; the `23.0.1` namespaces
in FXML files are not dependency version declarations.

On macOS, run the tests from a logged-in desktop session:

```bash
mvn -B -ntp clean verify
```

On Linux without a display, install Xvfb and the JavaFX runtime libraries
(package names below are for Ubuntu 24.04):

```bash
sudo apt-get update
sudo apt-get install -y xvfb xauth libgtk-3-0t64 libxtst6 libasound2t64
xvfb-run -a -s "-screen 0 1280x1024x24 -nolisten tcp" mvn -B -ntp clean verify
```

The same Surefire run executes the existing service unit tests and
`AddWordsControllerTest`. The UI tests load the production `add-words.fxml`,
including its toolbar, and exercise the real controller and word service.
Only the repository and the toolbar's stage manager are mocked. The test
fixture starts JavaFX directly through TestFX, without a Spring application
context or a database. The existing Maven AOT processing step remains enabled.

UI tests cover initialization and table columns, entering and adding a word
(including normalization, table refresh, form reset and clearing a previous
error), and validation errors that preserve the form and existing rows.
FXML loading and UI assertions run on the JavaFX Application Thread; TestFX
drives the mouse and text input and manages the stage lifecycle. No fixed sleeps
or display-dependent test skips are used. A missing display or an assertion
failure fails the build. Surefire writes results to `target/surefire-reports`.

Only `testfx-junit5:4.0.18` is added; it brings `testfx-core` transitively.
JUnit 5, Mockito and assertion libraries already come from Spring Boot's test
starter. Surefire opens `com.sun.javafx.application` for TestFX's toolkit
initialization. Linux uses a normal JavaFX toolkit under Xvfb, with no Monocle
dependency or headless profile; macOS uses its native desktop toolkit.

The Quality workflow runs `verify` under Xvfb on pushes to `main` and pull
requests targeting `main`. Both kinds of tests use Surefire's default `*Test`
discovery, so they run together without a separate UI job or opt-in profile.
SonarQube Cloud's existing fork restrictions and quality gate, and the Trivy
security scan, remain enabled.

### Validation

Validated on 2026-11-14 in the Linux ARM64 Docker Sandbox with Liberica Full
25.0.5.0.3 (JavaFX 25.0.5.0.3), Maven 3.10.0 and TestFX 4.0.18. The complete
`clean verify` command above passed under Xvfb. Surefire reported:

| Tests | Executed | Passed | Failed | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: | ---: |
| Service unit tests | 15 | 15 | 0 | 0 | 0 |
| JavaFX controller tests | 3 | 3 | 0 | 0 | 0 |

A temporary incorrect title assertion on the JavaFX Application Thread made
Maven exit with code 1 (one test, one error, zero skipped); TestFX wraps the
assertion failure in a runtime exception. The original assertion was restored
before the final complete run. Running the initialization test without a display
also exited with code 1 (`Unable to open DISPLAY`), with zero tests skipped.
No test fixes or additional JavaFX module openings were needed. The sandbox
required local Maven proxy settings and trust for its proxy certificate; neither
changes the project's test or security configuration.

Native macOS execution and a hosted GitHub Actions run have not been executed
in this sandbox. The workflow's pull-request trigger and unconditional Xvfb
verification step were checked locally; the SonarQube and Trivy checks were
not run locally.

Reference implementations reviewed:

* [piano-basics-fx](https://github.com/des-felins/piano-basics-fx/tree/c0cd2e1f9286cd2e3898e27d24e48e40de1e1bb8)
  uses TestFX 4.0.18 with JUnit 5 and the JavaFX application package opening,
  but starts Spring in its UI tests.
* [raffle](https://github.com/code-with-bellsoft/raffle/tree/f0374bf6b68352ebd29165a188e67c8fea230c76)
  uses TestFX/JUnit 5 with a Java 21 Monocle CI profile. Its Monocle dependency
  and test-skipping defaults are not carried over here.

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
