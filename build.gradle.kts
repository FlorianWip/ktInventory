plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

group = "de.florianwip"

val tag = System.getenv("GITHUB_REF")?.split("/")?.last() ?: "2.0.0"
version = if (tag.startsWith("v")) tag.substring(1) else tag

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        url = uri("https://repo.flammenfuchs.de/public")
        name = "flammenfuchs-repo"
    }

}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly ("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "ktInventory" // change to your desired artifactId
        }
    }

    repositories {
        maven {
            name = "flammenfuchs-public"
            url = uri("https://repo.flammenfuchs.de/public")
            credentials {
                username = System.getenv("MAVEN_REPO_USERNAME")
                password = System.getenv("MAVEN_REPO_PASSWORD")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}