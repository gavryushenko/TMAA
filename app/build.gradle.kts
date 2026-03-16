import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// Tento soubor je hlavní build konfigurace modulu :app

// --- Deklarace pluginů ---
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.google.services) // Aktivuje načtení konfigurace Firebase pro modul aplikace
}


// --- Hlavní konfigurace Android aplikace ---
extensions.configure<ApplicationExtension> {
    namespace = "com.matvii.application" // Definuje jmenný prostor aplikace pro generovaný kód
    // Určuje verzi Android SDK, se kterou se aplikace kompiluje
    compileSdk = 36

    // Základní parametry aplikace
    defaultConfig {
        applicationId = "com.matvii.application" // Jedinečný identifikátor aplikace
        minSdk = 26                              // Minimální verze Androidu pro aplikaci
        targetSdk = 36                           // Verze Androidu, pro kterou je aplikace optimalizována
        versionCode = 1                          // Číselná verze aplikace, používá se při aktualizacích
        versionName = "1.0"                      // Verze aplikace viditelná pro uživatele

        // Nastavení testovacího runneru pro instrumentační testy
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API klíč bereme pouze z proměnné prostředí, aby nebyl uložen v repozitáři.
        val apiKey = providers.environmentVariable("OPENWEATHER_API_KEY").getOrElse("")
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$apiKey\"")
    }

    // Definice různých typů sestavení aplikace
    buildTypes {
        // Konfigurace pro produkční (release) verzi aplikace
        release {
            isMinifyEnabled = false // Vypíná minimalizaci a obfuskaci kódu v release verzi
            // Použití výchozích optimalizačních pravidel ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Nastavení verze Javy pro kompilaci projektu
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Verze Javy používanou ve zdrojovém kódu
        targetCompatibility = JavaVersion.VERSION_11 // Verze Javy pro generovaný bytecode
    }

    // Aktivace vybraných funkcí Android build systému
    buildFeatures {
        compose = true // Aktivuje podporu Jetpack Compose v projektu
        buildConfig = true
    }
}

// Sjednocení JVM targetu pro Java i Kotlin úlohy, aby build nepadal na nekompatibilitě.
extensions.configure<KotlinAndroidProjectExtension> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

// Seznam všech knihoven, které aplikace používá
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    add("kapt", libs.androidx.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
