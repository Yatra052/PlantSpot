plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.plantspot"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.plantspot"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.database.ktx)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//  /  implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
//
//        //text dimensions
//
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
//
//    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
//    //lifecycle
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
//    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
//
////    image slider
//    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
//
//
//    //shimmer effect
//    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

    //room database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")

    // phone pay
//    implementation("phonepe.intentsdk.android.release:IntentSDK:2.3.0")
//    implementation ("phonepe.intentsdk.android.release:IntentSDK:2.4.3")
    implementation("com.google.firebase:firebase-storage-ktx:19.1.0")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.firebase:firebase-auth")
//    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
//    compile ("com.android.support:support-core-utils:27.1.0")


    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.2")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation("phonepe.intentsdk.android.release:IntentSDK:2.4.3")

}
