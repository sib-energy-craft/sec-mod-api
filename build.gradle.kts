import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("maven-publish")
    id("java")
    id("jacoco")
}

val modVersion = project.property("mod_version") as String
val minecraftVersion = project.property("minecraft_version") as String

version = "$modVersion-$minecraftVersion"
group = project.property("maven_group") as String

val targetJavaVersion = (project.property("jdk_version") as String).toInt()
val javaVersion = JavaVersion.toVersion(targetJavaVersion)

allprojects {
    apply(plugin = "fabric-loom")
    apply(plugin = "java")

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    repositories {
        mavenCentral()
        maven(url = "https://nexus.sibmaks.ru/repository/maven-snapshots/")
        maven(url = "https://nexus.sibmaks.ru/repository/maven-releases/")
    }

}

dependencies {
    compileOnly("org.projectlombok:lombok:${project.property("lombok_version")}")
    annotationProcessor("org.projectlombok:lombok:${project.property("lombok_version")}")

    // To change the versions, see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    modImplementation("com.github.sib-energy-craft:energy-api:${project.property("sec_energy_api_version")}")
    modImplementation("com.github.sib-energy-craft:sec-utils:${project.property("sec_utils_version")}")
    modImplementation("com.github.sib-energy-craft:sec-network:${project.property("sec_network_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    val properties = project.properties.mapKeys { it.key }
        .mapValues { it.value.toString() }

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release = targetJavaVersion
    }
}

java {
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")}" }
    }
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to project.property("mod_name"),
                "Specification-Vendor" to project.property("mod_author"),
                "Specification-Version" to version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to project.property("mod_author"),
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Build-On-Minecraft" to minecraftVersion
            )
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                packaging = "jar"
                url = "https://github.com/sib-energy-craft/sec-mod-api"

                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://www.mit.edu/~amini/LICENSE.md")
                    }
                }

                scm {
                    connection.set("scm:https://github.com/sib-energy-craft/sec-mod-api.git")
                    developerConnection.set("scm:git:ssh://github.com/sib-energy-craft")
                    url.set("https://github.com/sib-energy-craft/sec-mod-api")
                }

                developers {
                    developer {
                        id.set("sibmaks")
                        name.set("Maksim Drobyshev")
                        email.set("sibmaks@vk.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesUrl = uri("https://nexus.sibmaks.ru/repository/maven-releases/")
            val snapshotsUrl = uri("https://nexus.sibmaks.ru/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = project.findProperty("nexus_username")?.toString() ?: System.getenv("NEXUS_USERNAME")
                password = project.findProperty("nexus_password")?.toString() ?: System.getenv("NEXUS_PASSWORD")
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sib-energy-craft/sec-mod-api")
            credentials {
                username = project.findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key")?.toString() ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}