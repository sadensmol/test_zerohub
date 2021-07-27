import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  id("org.springframework.boot") version "2.5.3"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("com.google.protobuf") version "0.8.17"
  kotlin("jvm") version "1.5.21"
  kotlin("plugin.spring") version "1.5.21"
  idea
}

group = "com.zerohub.challenge"
java.sourceCompatibility = JavaVersion.VERSION_13

val grpcVersion = "1.39.0"
val grpcKotlinVersion = "1.1.0"
val protobufVersion = "3.17.3"
val grpcSpringVersion = "2.12.0.RELEASE"


repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation ("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")

  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

  implementation("io.grpc:grpc-protobuf:$grpcVersion")
  implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
  implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

  implementation ("net.devh:grpc-server-spring-boot-starter:$grpcSpringVersion")

  testImplementation("org.springframework.boot:spring-boot-starter-test"){
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("io.grpc:grpc-testing:1.36.0")
  testImplementation("net.devh:grpc-client-spring-boot-starter:$grpcSpringVersion")
}


tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging{
    events(FAILED,SKIPPED,STANDARD_OUT,STANDARD_ERROR)

    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
  }
}


tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "13"
  }
}
protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:$protobufVersion"
  }
  plugins {
    id("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
    }
    id("grpckt") {
      artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
    }
  }
  generateProtoTasks {
    all().forEach {
      it.plugins {
        id("grpc")
        id("grpckt")
      }
    }
  }
}