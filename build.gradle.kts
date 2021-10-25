import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.2.70"
val seleniumVersion = "3.141.59"

plugins {
    kotlin("jvm").version("1.2.70")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.7.1")
    id("com.gradle.build-scan").version("2.4.2")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
}

configurations.all {
    resolutionStrategy {
        activateDependencyLocking()
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "commons-codec:commons-codec" -> useVersion("1.11")
                "org.jetbrains:annotations" -> useVersion("13.0")
                "org.slf4j:slf4j-api" -> useVersion("1.7.25")
                "org.testcontainers:testcontainers" -> useVersion("1.15.1")
                "org.testcontainers:selenium" -> useVersion("1.15.1")
                "net.java.dev.jna:jna" -> useVersion("5.5.0")
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
                "org.seleniumhq.selenium" -> useVersion(seleniumVersion)
            }
        }
    }
}

dependencies {
    api("com.github.stephenc.jcip:jcip-annotations:1.0-1")
    api(webdriver("selenium-api"))
    api("com.atlassian.performance:selenium-js:[1.0.0,2.0.0)")

    implementation(webdriver("selenium-support"))
    implementation(webdriver("selenium-chrome-driver"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.glassfish:javax.json:1.1")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.atlassian.performance.tools:concurrency:[1.0.0,2.0.0)")
    listOf(
        "api",
        "core",
        "slf4j-impl",
        "jul"
    ).map { module ->
        "org.apache.logging.log4j:log4j-$module:2.10.0"
    }.forEach { implementation(it) }
    testCompile("org.assertj:assertj-core:3.11.0")
    testCompile("com.atlassian.performance.tools:io:[1.0.0,2.0.0)")
    testCompile("com.atlassian.performance.tools:docker-infrastructure:0.3.3")
    testCompile("junit:junit:4.12")
    testCompile("org.testcontainers:testcontainers:1.15.1")
}

tasks.test {
    testLogging.showStandardStreams = true
}

tasks
    .withType(KotlinCompile::class.java)
    .forEach { compileTask ->
        compileTask.apply {
            kotlinOptions.apply {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-Xjvm-default=enable")
            }
        }
    }

fun webdriver(module: String): String = "org.seleniumhq.selenium:$module:$seleniumVersion"

tasks.wrapper {
    gradleVersion = "5.1.1"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.getByName("test", Test::class).apply {
    exclude("**/*IT.class")
}

val testIntegration = task<Test>("testIntegration") {
    testLogging.exceptionFormat =  org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    testLogging.showStackTraces = true
    testLogging.showExceptions = true
    testLogging.showCauses = true
    testLogging.showStandardStreams = true //this is really only needed for Travis
    maxHeapSize = "2G"
    include("**/*IT.class")
}

tasks["check"].dependsOn(testIntegration)
