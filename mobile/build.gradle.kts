plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "dev.tberghuis.voicememos"
  compileSdk = 34

  defaultConfig {
    applicationId = "dev.tberghuis.wristrecorder"
    minSdk = 28
    targetSdk = 33
    versionCode = 10012
    versionName = "1.7.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
//    implementation project(path: ':common')
  implementation(project(":common"))

  // https://developer.android.com/jetpack/compose/bom/bom-mapping
//  val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
//  implementation(composeBom)
  implementation(platform(libs.androidx.compose.bom))
//  androidTestImplementation(composeBom)
  androidTestImplementation(platform(libs.androidx.compose.bom))
//  implementation("androidx.compose.material3:material3")
  implementation(libs.androidx.compose.material3)
//  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation(libs.androidx.ui.tooling.preview)
//  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation(libs.androidx.ui.tooling)
//  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  androidTestImplementation(libs.androidx.ui.test.junit4)
//  debugImplementation("androidx.compose.ui:ui-test-manifest")
  debugImplementation(libs.androidx.ui.test.manifest)

//  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(libs.androidx.activity.compose)
//  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
  implementation(libs.androidx.lifecycle.viewmodel.compose)
//  implementation("com.google.android.gms:play-services-wearable:18.1.0")
  implementation(libs.play.services.wearable)
//  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
  implementation(libs.kotlinx.coroutines.play.services)
//  implementation("androidx.datastore:datastore-preferences:1.0.0")
  implementation(libs.androidx.dataStore.preferences)

//  implementation("androidx.work:work-runtime-ktx:2.9.0")
  implementation(libs.androidx.work.ktx)
}