import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  id("com.google.gms.google-services")
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
}
