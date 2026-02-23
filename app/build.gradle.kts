// Tento soubor je hlavní build konfigurace modulu :app

// --- Deklarace pluginů ---
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}


// --- Hlavní konfigurace Android aplikace ---
android {
    namespace = "com.matvii.application" // Definuje jmenný prostor aplikace pro generovaný kód
    // Určuje verzi Android SDK, se kterou se aplikace kompiluje
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    // Základní parametry aplikace
    defaultConfig {
        applicationId = "com.matvii.application" // Jedinečný identifikátor aplikace
        minSdk = 26                              // Minimální verze Androidu pro aplikaci
        targetSdk = 36                           // Verze Androidu, pro kterou je aplikace optimalizována
        versionCode = 1                          // Číselná verze aplikace, používá se při aktualizacích
        versionName = "1.0"                      // Verze aplikace viditelná pro uživatele

        // Nastavení testovacího runneru pro instrumentační testy
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

// Seznam všech knihoven, které aplikace používá
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}