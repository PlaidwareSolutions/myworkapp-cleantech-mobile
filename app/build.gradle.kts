plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.rfidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rfidapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.exifinterface)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rxjava2)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)

    //Imageview
    implementation(libs.roundedimageview)
    implementation(libs.circleimageview)

    //Rx java
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    //Firebase ml kit
//  implementation(libs.mlkit.bom)
    implementation(libs.barcode.scanning)

    //Aar
    implementation(files("libs/xUtils-2.5.5.jar"))
    implementation(files("libs/jxl.jar"))
//  implementation(files("libs/cw-deviceapi20191022.jar"))
    implementation(files("libs/DeviceAPI20220518.aar"))
    implementation(files("libs/IGLBarDecoder.jar"))
    implementation(files("libs/API3_LIB-release.aar"))

    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    // hilt library
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:1.7.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:1.7.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:1.7.2")
}