//import org.jetbrains.kotlin.gradle.idea.proto.com.google.protobuf.compiler.version

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
    id("io.objectbox")
    id("kotlin-parcelize")
}

android {
    namespace = "com.projects.aware"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.projects.aware"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "aware-debug")
            manifestPlaceholders["icon"] = "@drawable/debug_app_icon.png"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    // object box
    implementation(libs.objectbox.kotlin.v420)

    implementation("co.yml:ycharts:2.1.0")

    // coil
    implementation(libs.coil.compose)

    // shimmer
    implementation(libs.compose.shimmer)

    // navigation
    implementation(libs.androidx.navigation.compose)

    // ok https
    implementation(libs.okhttp)

    // lottie
    implementation(libs.lottie.compose)

    // palette
    implementation(libs.androidx.palette.ktx)

    // color picker
    implementation(libs.compose.colorPicker)

    // room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}