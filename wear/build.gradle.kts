plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "dev.tberghuis.voicememos"
  compileSdk = 34

  defaultConfig {
    applicationId = "dev.tberghuis.wristrecorder"
    minSdk = 28
    targetSdk = 33
    versionCode = 19
    versionName = "1.7.7"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    create("debugTmp") {
      initWith(getByName("debug"))
      signingConfig = signingConfigs.getByName("debug")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    // https://www.jetpackcomposeversion.com/
    kotlinCompilerExtensionVersion = "1.5.5"
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.compose.ui:ui:1.7.3")
  implementation("androidx.compose.ui:ui-tooling-preview:1.7.3")
  implementation("androidx.compose.foundation:foundation:1.7.3")
  implementation("androidx.compose.material:material-icons-extended:1.7.3")
  implementation("androidx.lifecycle:lifecycle-service:2.8.6")

  val wear_compose_version = "1.4.0"
  implementation("androidx.wear.compose:compose-material:$wear_compose_version")
  implementation("androidx.wear.compose:compose-foundation:$wear_compose_version")
  implementation("androidx.wear.compose:compose-navigation:$wear_compose_version")

//  implementation (project (path: ':common'))
  implementation(project(":common"))

  implementation("com.google.accompanist:accompanist-permissions:0.36.0")
  implementation("androidx.wear:wear-input:1.1.0")
  implementation("com.google.android.gms:play-services-wearable:18.2.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
  implementation("androidx.work:work-runtime-ktx:2.9.1")
  implementation("androidx.wear:wear-ongoing:1.0.0")

  implementation("com.google.android.horologist:horologist-compose-layout:0.5.17")
  implementation("androidx.core:core-splashscreen:1.0.1")
}