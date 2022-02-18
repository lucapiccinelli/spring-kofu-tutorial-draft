plugins {
    kotlin("jvm") version "1.6.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"
val kotlinVersion = "1.6.10"
val kotestVersion = "5.1.0"
val junitVersion = "5.8.2"
val springVersion = "2.6.3"
val mockkVersion = "1.12.2"
val jacksonVersion = "2.12.4"

repositories {
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-mustache:$springVersion")
    implementation("org.springframework.fu:spring-fu-kofu:0.5.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.h2database:h2:1.4.200")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}