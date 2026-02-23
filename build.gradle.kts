// Soubor nejvyšší úrovně, do kterého můžete přidat konfigurační možnosti společné pro všechny podprojekty nebo moduly

plugins {
    alias(libs.plugins.android.application) apply false // Deklarace Android pluginu pro aplikaci
    alias(libs.plugins.kotlin.compose) apply false // Deklarace Kotlin/Compose pluginu
}