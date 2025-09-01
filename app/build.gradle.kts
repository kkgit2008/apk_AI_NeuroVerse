import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
  //  alias(libs.plugins.chaquo.py)
    kotlin("plugin.serialization") version "2.1.21"
}
val localPropertiesFile = rootProject.file("local.properties")

android {
    namespace = "com.dark.neuroverse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dark.neurov"
        minSdk = 33
        targetSdk = 36
        versionCode = 3
        versionName = "0.3-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true

        buildConfigField("String", "ALIAS", getProperty("ALIAS"))

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }
    }

    // ****** 设置自定义签名@Kotlin语法 ******
    signingConfigs {
        create("releaseee") {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            enableV1Signing = true // 启用 V1 签名
            enableV2Signing = true // 启用 V2 签名 (推荐，Android 7.0+)
            enableV3Signing = true // 启用 V3 签名 (推荐，Android 9+)
            if (localPropertiesFile.exists()) {
                localProperties.load(FileInputStream(localPropertiesFile))

                val storeFilePath = localProperties.getProperty("storeFile")
                val storePasswordValue = localProperties.getProperty("storePassword")
                val keyAliasValue = localProperties.getProperty("keyAlias")
                val keyPasswordValue = localProperties.getProperty("keyPassword")

                if (storeFilePath != null && storePasswordValue != null && 
                    keyAliasValue != null && keyPasswordValue != null) {
                    storeFile = file(storeFilePath)
                    storePassword = storePasswordValue
                    keyAlias = keyAliasValue
                    keyPassword = keyPasswordValue
                } else {
                    logger.error("There is sth wrong with file content:local.properties !")
                }
            } else {
                logger.error("File not exist:local.properties !")
            }
        }
    }

        buildTypes {
            debug {
                signingConfig = signingConfigs.getByName("releaseee")
                //applicationIdSuffix '.debug'
                versionNameSuffix = "-debug"
            }
            release {
                signingConfig = signingConfigs.getByName("releaseee")

            }
        }
    // ****** 设置自定义签名@Kotlin语法 ******

    buildTypes {
        release {
            isMinifyEnabled = false           // Enable code shrinking
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
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
//    flavorDimensions += "pyVersion"
//    productFlavors {
//        create("neuroV") { dimension = "pyVersion" }
//    }
}

//chaquopy {
//    productFlavors {
//        getByName("neuroV") { version = "3.10" }
//    }
//
//    sourceSets.getByName("main") {
//        setSrcDirs(listOf("src/main/python"))
//    }
//
//    defaultConfig {
//        pip {
//            install("python-docx")
//            install("python-pptx")
//            install("openpyxl")
//            install("pdfminer.six==20221105")
//            install("pandas")
//            install("chardet")
//        }
//    }
//}

dependencies {
   
//dependencies {
    implementation("androidx.multidex:multidex:2.0.1")
//}
 implementation(libs.androidx.lifecycle.runtime.compose)
    //CORE
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //PROJECTS
    implementation(project(":ai-module"))
    implementation(project(":ai-core"))
    implementation(project(":userData"))
    implementation(project(":updateManager"))
    implementation(project(":plugins"))

    //UTILS
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.kotlinx.serialization.json)

    //KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //CORE-UI-LIBS
    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.insets.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.navigation.animation)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.animation)
    implementation(libs.material)

    //TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    //DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

fun getProperty(value: String): String {
    return if (localPropertiesFile.exists()) {
        val localProps = Properties().apply {
            load(FileInputStream(localPropertiesFile))
        }
        localProps.getProperty(value) ?: "\"sample_val\""
    } else {
        System.getenv(value) ?: "\"sample_val\""
    }
}
