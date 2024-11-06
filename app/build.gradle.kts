plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
}

android {
    namespace = "com.example.firmwareflasher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.firmwareflasher"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("armeabi-v7a","arm64-v8a","x86", "x86_64") // Specify ABIs
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xuse-ir"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("com.github.mik3y:usb-serial-for-android:3.8.0")
//    implementation("androidx.core:core-ktx:1.7.0")
//    implementation("androidx.appcompat:appcompat:1.4.0")
}


chaquopy {
    version = "3.8" // or the desired Python version

    defaultConfig {
//        pyc {
//            src = false
//        }

        pip {
            install("/home/deck/Documents/Cynteract/pyserial/") // Replace with the actual path and filename
            install ("bitarray>=2.8.0")
            install("bitstring==3.1.6")
            install("esptool==4.7")
        }
    }
    productFlavors { }
    sourceSets { }
}

