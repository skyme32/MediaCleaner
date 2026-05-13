import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.okio)
            implementation(libs.multiplatform.settings)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}


compose.desktop {
    application {
        mainClass = "my.skyme32.mediacleaner.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MediaCleaner"
            packageVersion = "1.0.0"
            modules("java.sql")

            linux {
                shortcut = true
                appCategory = "Utility"
                menuGroup = "Office"
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/app_image.png"))
            }
            windows {
                shortcut = true
                menu = true
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/app.ico"))
            }
        }
    }
}

sqldelight {
    databases {
        create("MediaCleanerDatabase") {
            packageName.set("my.skyme32.mediacleaner.db")
        }
    }
}
