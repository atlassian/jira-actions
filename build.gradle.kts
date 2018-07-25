import java.net.URI
import net.linguica.gradle.maven.settings.LocalMavenSettingsLoader
import net.linguica.gradle.maven.settings.MavenSettingsPluginExtension
import org.jetbrains.dokka.gradle.DokkaTask

object Versions {
    const val kotlin = "1.2.30"
}

buildscript {
    repositories {
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("net.linguica.gradle:maven-settings-plugin:0.5")
    }
}

plugins {
    kotlin("jvm").version("1.2.30")
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
    id("pl.allegro.tech.build.axion-release").version("1.8.1")
}

apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

scmVersion {
    tag.prefix = "release"
}
project.version = scmVersion.version
project.group = "com.atlassian.test.performance"

tasks["release"].doFirst {
    if (scmVersion.scmPosition.branch != "master") {
        throw Exception("Releasing allowed only on master branch")
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"

    reportUndocumented = false
}

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(dokka)
}

publishing {
    val publication = publications.create("mavenJava", MavenPublication::class.java) {
        pom {
            scm {
                url.set("https://bitbucket.org/atlassian/jira-actions")
                connection.set("scm:git:git@bitbucket.org:atlassian/jira-actions.git")
                developerConnection.set("scm:git:git@bitbucket.org:atlassian/jira-actions.git")
            }
        }
    }
    val jar by tasks.getting(Jar::class) {
        into("META-INF/maven/${project.group}/${project.name}") {
            rename(".*", "pom.xml")
            from(tasks.withType(GenerateMavenPom::class.java).single())
        }
    }
    publication.apply {
        artifact(jar)
        artifact(sourcesJar)
        artifact(javadocJar)
    }
    if (scmVersion.version.endsWith("SNAPSHOT")) {
        repositories.add(project.repositories["atlassian-private-snapshot"])
    } else {
        repositories.add(project.repositories["atlassian-private"])
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "atlassian-public"
        url = URI("https://packages.atlassian.com/maven-external/")
    }
    maven {
        name = "atlassian-private"
        url = URI("https://packages.atlassian.com/maven-private/")
        credentials(
            atlassianPrivateFromEnv()
                ?: mavenCredentials()
                ?: throw Exception("Maven settings for '$name' are missing")
        )
    }
    maven {
        name = "atlassian-private-snapshot"
        url = URI("https://packages.atlassian.com/maven-private-snapshot")
        credentials(
            atlassianPrivateFromEnv()
                ?: mavenCredentials()
                ?: throw Exception("Maven settings for '$name' are missing")
        )
    }
}

fun atlassianPrivateFromEnv(): Action<in PasswordCredentials>? {
    val envUsername = System.getenv("atlassian_private_username")
    val envPassword = System.getenv("atlassian_private_password")
    if (envUsername == null || envPassword == null) {
        return null
    }
    return Action {
        username = envUsername
        password = envPassword
    }
}

fun MavenArtifactRepository.mavenCredentials(): Action<in PasswordCredentials>? {
    val settings = LocalMavenSettingsLoader(MavenSettingsPluginExtension(project)).loadSettings()
    val server = settings.getServer(name) ?: return null

    return Action {
        username = server.username
        password = server.password
    }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:${Versions.kotlin}")
    compile("org.glassfish:javax.json:1.1")
    compile("org.apache.commons:commons-math3:3.6.1")
    log4j(
        "api",
        "core",
        "slf4j-impl"
    ).forEach { compile(it) }
    testCompile("junit:junit:4.12")


    compile("com.atlassian.performance.tools:concurrency:0.0.1")
    compile("net.jcip:jcip-annotations:1.0")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:${Versions.kotlin}")
    compile("org.glassfish:javax.json:1.1")
    compile("org.apache.commons:commons-math3:3.6.1")
    testCompile("org.hamcrest:hamcrest-library:1.3")
    testCompile("junit:junit:4.12")
    log4j(
        "api",
        "core",
        "slf4j-impl"
    ).forEach { compile(it) }
    webdriver().forEach { compile(it) }
}

fun log4j(
    vararg modules: String
): List<String> = modules.map { module ->
    "org.apache.logging.log4j:log4j-$module:2.10.0"
}

fun webdriver(): List<String> = listOf(
    "selenium-support",
    "selenium-chrome-driver"
).map { module ->
    "org.seleniumhq.selenium:$module:3.11.0"
} + log4j("jul")

val wrapper = tasks["wrapper"] as Wrapper
wrapper.gradleVersion = "4.9"
wrapper.distributionType = Wrapper.DistributionType.ALL