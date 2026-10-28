plugins {
    java
    idea
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

group = "com.cheatbreaker"
version = "1.7.10"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 8
    options.compilerArgs.addAll(listOf("-proc:none", "-Xlint:-options", "-Xmaxerrs", "1000"))
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "Start")
    }
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1g"
}

val launcherDir = providers.gradleProperty("cb.launcherDir")
    .orElse(providers.environmentVariable("APPDATA").map { "$it/CheatBreaker" })
val gameRunDir = layout.projectDirectory.dir("run")

val prepareGameRun = tasks.register("prepareGameRun") {
    group = "application"
    description = "Creates run/mc and copies the CheatBreaker launcher's natives (without the OpenAL libraries, sound uses the Java Sound backend) and assets into run/ (only files that are missing or changed)"
    val launcher = launcherDir
    val run = gameRunDir
    doLast {
        val downloads = file(launcher.getOrElse("")).resolve("downloads")
        require(downloads.resolve("natives").isDirectory && downloads.resolve("assets/indexes/1.7.10.json").isFile) {
            "CheatBreaker launcher data not found under $downloads. Run the CheatBreaker launcher once (it downloads 1.7.10), or pass -Pcb.launcherDir=<launcher data dir>"
        }
        run.asFile.resolve("mc").mkdirs()
        for (name in listOf("natives", "assets")) {
            val source = downloads.resolve(name)
            val target = run.asFile.resolve(name)
            source.walkTopDown().filter { it.isFile && !it.name.contains("openal", ignoreCase = true) }.forEach { file ->
                val copy = target.resolve(file.relativeTo(source).path)
                if (!copy.isFile || copy.length() != file.length()) {
                    copy.parentFile.mkdirs()
                    file.copyTo(copy, overwrite = true)
                }
            }
        }
    }
}

tasks.register<JavaExec>("runGame") {
    group = "application"
    description = "Starts OpenCheatBreaker with an offline test account (game dir: run/mc)"
    dependsOn(prepareGameRun)
    mainClass = "Start"
    classpath = sourceSets.main.get().runtimeClasspath
    workingDir = gameRunDir.asFile
    javaLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(25) }
    val run = gameRunDir
    val username = providers.gradleProperty("cb.username").orElse("Developer")
    val width = providers.gradleProperty("cb.width").orElse("854")
    val height = providers.gradleProperty("cb.height").orElse("480")
    val extraJvm = providers.gradleProperty("cb.extraJvmArgs").orElse("")
    jvmArgs("-XX:-UseAdaptiveSizePolicy", "-XX:-OmitStackTraceInFastThrow", "-Xmx4096M", "-Xms1024M", "-Djava.locale.providers=JRE", "--enable-native-access=ALL-UNNAMED")
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        val natives = run.asFile.resolve("natives").absolutePath
        listOf("-Djava.library.path=$natives", "-Dorg.lwjgl.librarypath=$natives", "-Dnet.java.games.input.librarypath=$natives") +
            extraJvm.get().split(" ").filter { it.isNotBlank() }
    })
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--username", username.get(),
            "--gameDir", run.asFile.resolve("mc").absolutePath,
            "--assetsDir", run.asFile.resolve("assets").absolutePath,
            "--uuid", "00000000000000000000000000000001",
            "--accessToken", "0",
            "--userType", "legacy",
            "--width", width.get(),
            "--height", height.get(),
            "--installationID", "00000000-0000-0000-0000-000000000000",
            "--websocketToken", "0",
            "--launcherVersion", "2026.10.0"
        )
    })
}
