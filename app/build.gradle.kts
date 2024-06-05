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
    implementation(platform(libs.firebase.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // ViewModel, LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //http
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //tts
    implementation(libs.play.services.vision)

}
