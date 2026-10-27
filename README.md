<p align="center">
  <img src="docs/logo.png" alt="CheatBreaker" width="160">
</p>

<h1 align="center">OpenCheatBreaker</h1>

## Build and run

Requires JDK 25 and network access to Maven Central (test dependencies). The game's natives and assets are copied from an installed CheatBreaker launcher (`%APPDATA%/CheatBreaker/downloads`); run the launcher once, or pass `-Pcb.launcherDir=<launcher data dir>`.

```
./gradlew jar          # output is in build/libs/
./gradlew test
./gradlew runGame      # copies the natives and assets into run/ and starts the game with an offline test account (game data in run/mc/)
```

IntelliJ IDEA: `File > Open` and select this directory. The project settings (`.idea/`) and the **OpenCheatBreaker** run configuration (`.run/OpenCheatBreaker.run.xml`) are included in the repository; set the Gradle JVM to JDK 25. The working directory is `run/` and game data lives in `run/mc/`.

Game sounds use Paulscode's pure Java Sound backend (`LibraryJavaSound`) instead of OpenAL, so the OpenAL native libraries are not needed (LWJGL's OpenAL could not be loaded from a path with non-ASCII characters, which left the game silent). Only the LWJGL and JInput natives for display and input remain in `run/natives`.

[![QQ Group 1078719379](https://img.shields.io/badge/QQ%20Group-1078719379-12B7F5?logo=tencentqq&logoColor=white)](https://qm.qq.com/q/OnrCkjX7Y4)
