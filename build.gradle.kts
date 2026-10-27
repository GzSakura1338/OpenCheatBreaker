import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.security.MessageDigest
import java.util.zip.ZipFile

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
version = "1.8.9"

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
        require(downloads.resolve("natives").isDirectory && downloads.resolve("assets/objects").isDirectory) {
            "CheatBreaker launcher data not found under $downloads. Run the CheatBreaker launcher once, or pass -Pcb.launcherDir=<launcher data dir>"
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

val releaseId = "OpenCheatBreaker-${project.version}"
val releaseRoot = layout.buildDirectory.dir("release/$releaseId")

val releasePackage = tasks.register("releasePackage") {
    group = "distribution"
    description = "Builds a launcher-style version folder (versions/, libraries/, natives/, start.bat) in build/release"
    dependsOn(tasks.jar)
    val jarFile = tasks.jar.flatMap { it.archiveFile }
    val libsDir = layout.projectDirectory.dir("libs")
    val releaseSource = layout.projectDirectory.dir("release")
    val outputRoot = releaseRoot
    inputs.file(jarFile)
    inputs.dir(libsDir)
    inputs.dir(releaseSource)
    outputs.dir(outputRoot)
    doLast {
        val root = outputRoot.get().asFile
        root.deleteRecursively()

        fun sha1(file: File): String =
            MessageDigest.getInstance("SHA-1").digest(file.readBytes()).joinToString("") { "%02x".format(it) }

        fun place(artifact: Map<*, *>): File {
            val path = artifact["path"] as String
            val name = path.substringAfterLast('/')
            val source = libsDir.file(name).asFile.takeIf { it.isFile } ?: releaseSource.file("libraries/$path").asFile
            if (!source.isFile) {
                throw GradleException("Library $path is not in libs/ or release/libraries/")
            }
            if (sha1(source) != artifact["sha1"]) {
                throw GradleException("Checksum of $path does not match release/version-template.json")
            }
            val target = File(root, "libraries/$path")
            target.parentFile.mkdirs()
            source.copyTo(target, overwrite = true)
            return target
        }

        val template = LinkedHashMap((JsonSlurper().parse(releaseSource.file("version-template.json").asFile, "UTF-8") as Map<*, *>).entries.associate { it.key as String to it.value })
        template["id"] = releaseId

        val classpath = mutableListOf<String>()
        val nativeJars = mutableListOf<File>()
        for (library in template["libraries"] as List<*>) {
            val downloads = (library as Map<*, *>)["downloads"] as Map<*, *>
            (downloads["artifact"] as Map<*, *>?)?.let {
                place(it)
                classpath += "libraries/" + it["path"]
            }
            ((downloads["classifiers"] as Map<*, *>?)?.get("natives-windows") as Map<*, *>?)?.let { nativeJars += place(it) }
        }

        val versionDir = File(root, "versions/$releaseId")
        versionDir.mkdirs()
        jarFile.get().asFile.copyTo(File(versionDir, "$releaseId.jar"), overwrite = true)
        classpath += "versions/$releaseId/$releaseId.jar"
        File(versionDir, "$releaseId.json").writeText(JsonOutput.prettyPrint(JsonOutput.toJson(template)) + "\n")

        val nativesDir = File(root, "natives")
        nativesDir.mkdirs()
        for (nativeJar in nativeJars) {
            ZipFile(nativeJar).use { zip ->
                for (entry in zip.entries().asSequence()) {
                    val name = entry.name
                    if (entry.isDirectory || name.startsWith("META-INF/") || name.contains("openal", ignoreCase = true)) {
                        continue
                    }
                    File(nativesDir, name).outputStream().use { out -> zip.getInputStream(entry).use { it.copyTo(out) } }
                }
            }
        }

        val runtimeDir = File(root, "runtime")
        runtimeDir.mkdirs()
        File(runtimeDir, "java.args").writeText(
            listOf(
                "-Djava.library.path=natives",
                "-Xmx2G",
                "-XX:-UseAdaptiveSizePolicy",
                "-XX:-OmitStackTraceInFastThrow",
                "-Djava.locale.providers=JRE",
                "-cp",
                classpath.joinToString(";")
            ).joinToString("\n") + "\n"
        )

        File(root, "start.bat").writeText(releaseSource.file("start.bat").asFile.readText().replace("\r\n", "\n").replace("\n", "\r\n"))
        releaseSource.file("README.txt").asFile.copyTo(File(root, "README.txt"), overwrite = true)
        File(root, "tools").mkdirs()
        releaseSource.file("tools/download-assets.ps1").asFile.copyTo(File(root, "tools/download-assets.ps1"), overwrite = true)
    }
}

tasks.register<Zip>("releaseZip") {
    group = "distribution"
    description = "Packs the version folder into build/distributions/$releaseId.zip"
    dependsOn(releasePackage)
    from(layout.buildDirectory.dir("release")) {
        include("$releaseId/**")
    }
    archiveFileName = "$releaseId.zip"
    destinationDirectory = layout.buildDirectory.dir("distributions")
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
