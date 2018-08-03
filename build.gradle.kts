object Versions {
    const val kotlin = "1.2.30"
}

plugins {
    kotlin("jvm").version("1.2.30")
    id("com.atlassian.performance.tools.gradle-release").version("0.0.2")
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