plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "dev.tberghuis.voicememos"
  compileSdk = 35

  defaultConfig {
    applicationId = "dev.tberghuis.wristrecorder"
    minSdk = 28
    targetSdk = 34
    versionCode = 21
    versionName = "1.9.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  flavorDimensions += "flavor"
  productFlavors {
    create("default") {
      isDefault = true
      dimension = "flavor"
    }
    create("backoverride") {
      dimension = "flavor"
      versionNameSuffix = "-backoverride"
      applicationIdSuffix = ".backoverride"
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
    buildConfig = true
  }
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  implementation(project(":common"))
  implementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(platform(libs.androidx.compose.bom))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.compose.foundation)
  implementation(libs.androidx.compose.material.iconsExtended)
  implementation(libs.androidx.lifecycle.service)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.wear.compose.navigation)
  implementation(libs.accompanist.permissions)
  implementation(libs.wear.input)
  implementation(libs.play.services.wearable)
  implementation(libs.kotlinx.coroutines.play.services)
  implementation(libs.androidx.work.ktx)
  implementation(libs.androidx.wear.ongoing)
  implementation(libs.horologist.compose.layout)
  implementation(libs.androidx.core.splashscreen)

  // whats the deal???
//    wearApp(project(":wear"))
}