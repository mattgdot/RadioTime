plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.app.radiotime"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.radiotime.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 53
        versionName = "3.0-prod"

        resValue("string", "app_id", "ca-app-pub-3940256099942544~3347511713")
        resValue ("string", "interstitial_ad_id", "ca-app-pub-3940256099942544/1033173712")
        resValue ("string", "revcat_key", "REVENUECAT_KEY")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation("com.google.android.gms:play-services-ads:24.5.0")
    implementation("com.google.firebase:firebase-crashlytics:20.0.0")
    implementation("com.google.firebase:firebase-analytics:23.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.07.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.compose.material:material-icons-extended")
    implementation ("com.google.android.play:review-ktx:2.0.2")
    implementation ("io.github.mertceyhan:compose-inappreviews:1.0.0")
    implementation("com.google.dagger:hilt-android:2.57")
    kapt("com.google.dagger:hilt-android-compiler:2.57")
    implementation("androidx.room:room-runtime:2.7.2")
    annotationProcessor("androidx.room:room-compiler:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.room:room-rxjava2:2.7.2")
    implementation("androidx.room:room-rxjava3:2.7.2")
    implementation("androidx.room:room-guava:2.7.2")
    testImplementation("androidx.room:room-testing:2.7.2")
    implementation("androidx.room:room-paging:2.7.2")
    implementation ("com.google.code.gson:gson:2.13.1")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("androidx.work:work-runtime:2.10.3")
    implementation ("androidx.work:work-runtime-ktx:2.10.3")
    implementation ("androidx.work:work-rxjava2:2.10.3")
    implementation ("androidx.work:work-gcm:2.10.3")
    androidTestImplementation ("androidx.work:work-testing:2.10.3")
    implementation ("androidx.work:work-multiprocess:2.10.3")
    implementation ("com.revenuecat.purchases:purchases:7.0.1")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0")
    implementation("androidx.media3:media3-exoplayer-rtsp:1.8.0")
    implementation("androidx.media3:media3-exoplayer-ima:1.8.0")
    implementation("androidx.media3:media3-datasource-cronet:1.8.0")
    implementation("androidx.media3:media3-datasource-okhttp:1.8.0")
    implementation("androidx.media3:media3-datasource-rtmp:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-ui-leanback:1.8.0")
    implementation("androidx.media3:media3-session:1.8.0")
    implementation("androidx.media3:media3-extractor:1.8.0")
    implementation("androidx.media3:media3-cast:1.8.0")
    implementation("androidx.media3:media3-exoplayer-workmanager:1.8.0")
    implementation("androidx.media3:media3-transformer:1.8.0")
    implementation("androidx.media3:media3-test-utils:1.8.0")
    implementation("androidx.media3:media3-database:1.8.0")
    implementation("androidx.media3:media3-decoder:1.8.0")
    implementation("androidx.media3:media3-datasource:1.8.0")
    implementation("androidx.media3:media3-common:1.8.0")

    implementation ("com.google.accompanist:accompanist-permissions:0.37.3")
}