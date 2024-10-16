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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    // https://www.jetpackcomposeversion.com/
    kotlinCompilerExtensionVersion = "1.5.5"
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
  val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
  implementation(composeBom)
  androidTestImplementation(composeBom)
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-test-manifest")

  implementation("androidx.activity:activity-compose:1.8.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
  implementation("com.google.android.gms:play-services-wearable:18.1.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
  implementation("androidx.datastore:datastore-preferences:1.0.0")

  implementation("androidx.work:work-runtime-ktx:2.9.0")
}