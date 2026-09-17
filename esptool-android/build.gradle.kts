plugins {
    alias(libs.plugins.android.library)
    id("com.chaquo.python") version "17.0.0"
}

android {
    namespace = "com.xcarlost.esptool_android"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(platform(libs.kotlin.bom))
}

chaquopy {
    defaultConfig {
        version = "3.13"
        pip {
            install("git+https://github.com/xCarlost/pyserial.git@b6adda109d814499a65c671ff60a888d479f3a3d")
            install("bitarray==3.0.0")
            install("bitstring==3.1.6")
            install("cryptography==42.0.8")
            install("ecdsa==0.19.1")
            install("reedsolo==1.7.0")
            install("PyYAML==6.0.3")
            install("intelhex==2.3.0")
            install("argcomplete>=3,<4")
            install("git+https://github.com/espressif/esptool.git@v4.8.1")
        }
    }
}