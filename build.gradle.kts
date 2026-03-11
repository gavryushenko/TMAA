// Soubor nejvyšší úrovně, do kterého můžete přidat konfigurační možnosti společné pro všechny podprojekty nebo moduly

plugins {
    alias(libs.plugins.android.application) apply false // Deklarace Android pluginu pro aplikaci
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Deklarace Kotlin/Compose pluginu
    id("org.jetbrains.kotlin.kapt") version "2.0.21" apply false
    alias(libs.plugins.google.services) apply false // Firebase plugin zpřístupní hodnoty z google-services.json
}
