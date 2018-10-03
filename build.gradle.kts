object Versions {
    const val kotlin = "1.2.30"
}

plugins {
    kotlin("jvm").version("1.2.30")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.4.3")
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "commons-codec:commons-codec" -> useVersion("1.10")
            }
        }
    }
}

dependencies {
    api("com.github.stephenc.jcip:jcip-annotations:1.0-1")
    api(webdriver("selenium-api"))

    implementation(webdriver("selenium-support"))
    implementation(webdriver("selenium-chrome-driver"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jre8:${Versions.kotlin}")
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

    testCompile("org.hamcrest:hamcrest-library:1.3")
    testCompile("junit:junit:4.12")
}

fun webdriver(module: String): String = "org.seleniumhq.selenium:$module:3.11.0"

task<Wrapper>("wrapper") {
    gradleVersion = "4.9"
    distributionType = Wrapper.DistributionType.ALL
}
