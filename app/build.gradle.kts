import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// local.properties에서 값 불러오기
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val openAiApiKey: String = localProperties.getProperty("OPENAI_API_KEY") ?: ""



android {
    namespace = "com.example.moneyfy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moneyfy"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary=true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("com.kizitonwose.calendar:view:2.6.2")
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")

    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    // 중복 제거된 navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // 버전 통일
    implementation(libs.core.ktx)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit (서버 통신 라이브러리) + OpenAI GPT 연동용
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson 변환기 (JSON 직렬화/역직렬화)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
}