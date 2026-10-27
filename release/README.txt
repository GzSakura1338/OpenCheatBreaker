OpenCheatBreaker 1.8.9

This folder is a Minecraft-style version package.

Run it directly (Windows)
  1. Install Java (JDK 25 recommended).
  2. Double-click start.bat. On the first run it downloads the Minecraft 1.8 assets (about 115 MB)
     into assets\ unless they already exist in %APPDATA%\.minecraft\assets.
  3. Game data (saves, options, resource packs) lives in game\.
  Environment variables: OCB_USERNAME (player name, default Player), OCB_JAVA_OPTS (extra JVM options),
  JAVA_HOME (which Java to use).

Use it from a launcher (HMCL, PCL, the official launcher, ...)
  Copy the versions\ and libraries\ folders into your .minecraft folder (merge them), then pick the
  version "OpenCheatBreaker-1.8.9". The launcher downloads the assets and, on macOS and Linux, the
  native libraries. The Windows natives and all other libraries are already included.
