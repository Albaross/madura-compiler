plugins {
    kotlin("multiplatform") version "1.9.21"
}

kotlin {
    jvm()
    js(IR).browser {
        distribution {
            outputDirectory = File("$projectDir/targetJs/")
        }
    }

    sourceSets {
        jvmTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
        }
        val commonMain by getting
        val jsMain by getting
    }
}

repositories {
    mavenCentral()
}