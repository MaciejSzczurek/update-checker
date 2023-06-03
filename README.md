# Update Checker

A small tool for checking updates for a variety of applications and libraries from various sites, portals or
repositories. Spring Framework and JavaFX were used to create the application.

## Requirements

- Java 20
- Google Chrome 114

## Installation

Compiled JAR files for Windows can be downloaded from the releases page. For other systems, compile the application
earlier using the command:

```bash
./gradlew compileAndMove -PmoveDir=[path to install update-checker.jar]
```

Before compiling or after pulling the latest changes from the repository, run the `downloadChromeDriver` task, which
will download and patch the chrome driver with the command:

```bash
./gradlew downloadChromeDriver
```

## Usage

The application can be run from the command line.

```bash
java -jar update-checker.jar
```

The application allows you to configure some parameters such as the number of threads in the thread pool and the pool of
Chrome browsers running at the same time.

| Parameter                            | Description                        | Default                 |
| ------------------------------------ | ---------------------------------- | ----------------------- |
| spring.task.execution.pool.core-size | Core number of threads             | `4 * [number of cores]` |
| webdriver.pool-size                  | Number of running Chrome instances | `4`                     |

Sample addresses of sites on which updates are checked for each type of application can be found in the unit tests.

## Contributing

Pull requests are always welcome.

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
