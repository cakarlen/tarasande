import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import su.mandora.codechecker.CodeChecker

plugins {
    id("fabric-loom")
    id("org.jetbrains.kotlin.jvm")
}

val tarasandeName = "tarasande"
version = property("tarasande_version")!!
group = "su.mandora"

base {
    archivesName = tarasandeName
}

loom {
    accessWidenerPath = file("src/main/resources/$tarasandeName.accesswidener")

    mods {
        create(archivesName.get()) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val dependency: Configuration by configurations.creating

configurations {
    api.get().extendsFrom(dependency)
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    dependency("io.netty:netty-handler-proxy:4.1.82.Final") // Match with mc version
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependency.forEach {
            from(zipTree(it))
        }
        from("LICENSE") {
            rename { "${it}_${tarasandeName}" }
        }
    }

    compileKotlin.get().kotlinOptions.jvmTarget = "17"

    withType<JavaCompile> {
        options.release.set(17)
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.register("checkCode") {
    group = tarasandeName
    dependsOn("build")
    doLast {
        val codeChecker = CodeChecker(sourceSets.main.get())
        codeChecker.check()
    }
}

tasks.register("installPackages") {
    group = tarasandeName
    dependsOn("build")
    doLast {
        val modFolder = File("run", "mods")
        if (!modFolder.exists()) modFolder.mkdirs()

        subprojects.filter { it.name.startsWith("package") }.forEach { p ->
            val packageName = p.name + "-" + p.version + ".jar"
            val build = File(p.buildDir, "libs" + File.separator + packageName)
            val modDest = File(modFolder, packageName)
            if (build.exists()) {
                if (modDest.delete())
                    println("Deleted old $packageName")
                else
                    println("Failed to delete old $packageName version")
                if (build.renameTo(modDest))
                    println("Copied $packageName")
            }
        }
    }
}
