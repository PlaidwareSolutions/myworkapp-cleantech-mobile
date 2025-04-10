plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.rfidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rfidapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", getValue("prodBaseUrl", ""))
            buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
        }
        release {
            buildConfigField("String", "BASE_URL", getValue("prodBaseUrl", ""))
            buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")

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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.crashlytics.buildtools)
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
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)

    //Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    //GIF Drawable
    implementation(libs.android.gif.drawable)

    //==================== Networking ====================
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //==================== HTTP inspector ====================
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    implementation(libs.kotlinx.serialization.json.jvm)

    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

}

fun getValue(key: String, defaultValue: String): String {
    val value = project.findProperty(key) as String?
    return if (value != null) "\"$value\"" else "\"$defaultValue\""
}