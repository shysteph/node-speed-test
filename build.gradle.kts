plugins {
  id("java")
  id("me.champeau.jmh") version "0.7.3" // Check Gradle Plugin Portal for the latest version
}

group = "com.generalcode"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:6.0.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
}