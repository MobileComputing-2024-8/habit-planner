import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  id("com.google.gms.google-services")
  id("kotlin-kapt") // kapt 플러그인 추가

}

fun getApiKey(propertyKey: String): String {
  val properties = Properties()
  val localPropertiesFile = File(rootDir, "local.properties")
  if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
  }
  return properties.getProperty(propertyKey)
}

android {
  namespace = "com.example.habit_planner"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.habit_planner"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    buildConfigField("String", "OPENAI_API_KEY", "\"${getApiKey("OPENAI_API_KEY")}\"")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
    buildConfig = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)

  implementation(libs.firebase.messaging)

  testImplementation(libs.junit)
  implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)


  implementation("androidx.core:core-ktx:1.13.1")

  val room_version = "2.6.1"

  implementation("androidx.room:room-runtime:$room_version")
  annotationProcessor("androidx.room:room-compiler:$room_version")

  // To use Kotlin annotation processing tool (kapt)
  kapt("androidx.room:room-compiler:$room_version")
  implementation("androidx.room:room-ktx:2.6.1")

  val lifecycle_version = "2.8.1"
  val arch_version = "2.2.0"

  // ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
  // ViewModel utilities for Compose
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
  // LiveData
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
  // Lifecycles only (without ViewModel or LiveData)
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
  // Lifecycle utilities for Compose
  implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

  // Saved state module for ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
  // alternately - if using Java8, use the following instead of lifecycle-compiler
  implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

  // optional - helpers for implementing LifecycleOwner in a Service
  implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")

  // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
  implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")

  // optional - ReactiveStreams support for LiveData
  implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")

  // optional - Test helpers for LiveData
  testImplementation("androidx.arch.core:core-testing:$arch_version")

  // optional - Test helpers for Lifecycle runtime
  testImplementation ("androidx.lifecycle:lifecycle-runtime-testing:$lifecycle_version")

  // Coroutines
//  implementation(libs.kotlinx.coroutines.core)
//  implementation(libs.kotlinx.coroutines.android)

}
