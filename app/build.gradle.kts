import javax.inject.Inject

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}

val wordSearchBetaCandidate = providers.gradleProperty("wordSearchBetaCandidate")
    .map(String::toBooleanStrict).orElse(false).get()

android {
    namespace = "com.playereden.gridmates.wordsearch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.playereden.gridmates.wordsearch"
        minSdk = 26
        targetSdk = 35
        versionCode = providers.gradleProperty("qaVersionCode").orNull?.toInt() ?: 108
        versionName = "0.9.64-ws60-book-page-readability"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Test-only public QA signer: NEVER use for a release or Google Play.
    signingConfigs {
        create("wordSearchQa") {
            storeFile = rootProject.file("qa-signing/wordsearch-qa.p12")
            storeType = "pkcs12"
            storePassword = "android" // public QA-only credential
            keyAlias = "wordsearchQa"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("wordSearchQa")
            // A QA build installs alongside the eventual production Word Search.
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
        }
        getByName("release") {
            isMinifyEnabled = false
            // The beta uses the release source set and installs alongside QA.
            if (wordSearchBetaCandidate) applicationIdSuffix = ".beta"
        }
        create("betaPlaytest") {
            applicationIdSuffix = ".beta"
            isDebuggable = false
        }
    }

    sourceSets.named("betaPlaytest") {
        // Review the same catalog and artwork as QA under the beta identity.
        java.srcDir("src/debug/java")
        assets.srcDir("src/debug/assets")
    }

    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

abstract class StageFrozenPreviewAssets : DefaultTask() {
    @get:InputDirectory abstract val snapshots: DirectoryProperty
    @get:OutputDirectory abstract val output: DirectoryProperty
    @get:Inject abstract val files: FileSystemOperations

    @TaskAction fun stage() {
        files.sync {
            from(snapshots)
            into(output)
        }
    }
}

val stageWs66PreviewAssets = tasks.register<StageFrozenPreviewAssets>("stageWs66PreviewAssets") {
    dependsOn(":core:generateWs66Preview")
    snapshots.set(project(":core").layout.buildDirectory.dir("generated/ws66-preview/assets"))
    output.convention(layout.buildDirectory.dir("generated/assets/ws66Preview"))
}

val stageWs73PreviewAssets = tasks.register<StageFrozenPreviewAssets>("stageWs73PreviewAssets") {
    dependsOn(":core:generateWs73Preview")
    snapshots.set(project(":core").layout.buildDirectory.dir("generated/ws73-preview/assets"))
    output.convention(layout.buildDirectory.dir("generated/assets/ws73Preview"))
}

val previewBuildTypes = setOf("debug", "betaPlaytest")

androidComponents.onVariants(androidComponents.selector().all()) { variant ->
    if (variant.buildType in previewBuildTypes) {
        variant.sources.assets?.addGeneratedSourceDirectory(stageWs66PreviewAssets, StageFrozenPreviewAssets::output)
        variant.sources.assets?.addGeneratedSourceDirectory(stageWs73PreviewAssets, StageFrozenPreviewAssets::output)
    }
}

androidComponents.onVariants(androidComponents.selector().withBuildType("debug")) { debug ->
    debug.outputs.forEach {
        it.versionCode.set(providers.gradleProperty("qaVersionCode").map(String::toInt).orElse(120))
        it.versionName.set("0.9.75-ws77-woodland-album-qa")
    }
}

androidComponents.onVariants(androidComponents.selector().withBuildType("betaPlaytest")) { playtest ->
    playtest.outputs.forEach {
        it.versionCode.set(125)
        it.versionName.set("0.9.75-beta.1-library-polish-playtest")
    }
}

androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { release ->
    if (wordSearchBetaCandidate) {
        release.outputs.forEach {
            it.versionCode.set(121)
            it.versionName.set("0.9.75-beta.1")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}

/**
 * Debug-only source-backed book art. Each QA build reproduces the exact 18
 * SVG prototypes from WS-12C/D/F before merging local Android assets.
 * No release variant references this task or assets; no online service used.
 */
val generateWs12QaArt by tasks.registering(Exec::class) {
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/render_family_review.py",
        "--tokens", "design/asset-pipeline/paperback-v1-tokens.json",
        "--output-dir", "app/src/debug/assets/ws12"
    )
}
// Decode verified SVGs at build time so playable cards use lightweight
// Compose PNGs, not 18 costly per-card WebViews. This is DEBUG ONLY.
val rasterizeWs12QaArt by tasks.registering(Exec::class) {
    dependsOn(generateWs12QaArt)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/rasterize_android_qa.py",
        "--input-dir", "app/src/debug/assets/ws12",
        "--output-dir", "app/src/debug/assets/ws12/raster"
    )
}
val previewAssetTasks = previewBuildTypes.map { "merge${it.replaceFirstChar(Char::uppercaseChar)}Assets" }.toSet()
tasks.matching { it.name in previewAssetTasks }.configureEach {
    dependsOn(rasterizeWs12QaArt)
}


/**
 * WS-12K — a second, independently hashed experimental illustration style.
 * The original quiet art and all saved games remain untouched.
 * No study artwork is included in release.
 */
val generateWs12FieldNotes by tasks.registering(Exec::class) {
    dependsOn(generateWs12QaArt)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/render_field_notes_study.py",
        "--tokens", "design/asset-pipeline/paperback-v1-tokens.json",
        "--original-dir", "app/src/debug/assets/ws12",
        "--output-dir", "app/src/debug/assets/ws12/alt/field-notes"
    )
}
val rasterizeWs12FieldNotes by tasks.registering(Exec::class) {
    dependsOn(generateWs12FieldNotes)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/rasterize_android_qa.py",
        "--input-dir", "app/src/debug/assets/ws12/alt/field-notes",
        "--output-dir", "app/src/debug/assets/ws12/raster_field_notes"
    )
}
tasks.matching { it.name in previewAssetTasks }.configureEach {
    dependsOn(rasterizeWs12FieldNotes)
}


// Exact master artwork, fetched only at build time and verified on every build.
val startupAssetsDir = layout.buildDirectory.dir("generated/startupBranding/assets")
val prepareStartupBranding = tasks.register("prepareStartupBranding") {
    val pins = listOf(
        Triple("spudacus.png", "015645bffb612e0f4311ec5a2d29e9c8bad91289/branding/spudacus/v1.0/full-transparent.png", "74773dbda822161f73a639f959e8bce5c6e0b333a94ebeb8ee27f620d4995d25"),
        Triple("gridmates.png", "9affedc94ca5a6b896d36148f46f14a4e8218c3a/branding/gridmates/v1.0/runtime-transparent.png", "eef6d631cac0b9523f7e0dbebac229f9a2458d36fe2e73e111d251704d9ff292")
    )
    inputs.property("startupPins", pins.toString())
    outputs.dir(startupAssetsDir)
    outputs.upToDateWhen { false }
    doLast {
        val folder = startupAssetsDir.get().dir("branding/startup/v1").asFile
        folder.mkdirs()
        fun hash(bytes: ByteArray) = java.security.MessageDigest.getInstance("SHA-256")
            .digest(bytes).joinToString("") { "%02x".format(it.toInt() and 255) }
        for ((name, source, expected) in pins) {
            val dest = folder.resolve(name)
            if (!dest.isFile || hash(dest.readBytes()) != expected) {
                check(!gradle.startParameter.isOffline) { "Missing verified startup artwork: " + name }
                val connection = java.net.URI("https://raw.githubusercontent.com/EdenP-Development/Eden-Development-Standards/" + source).toURL()
                    .openConnection().apply { connectTimeout = 15000; readTimeout = 30000 }
                val bytes = connection.getInputStream().use { it.readNBytes(16 * 1024 * 1024 + 1) }
                check(bytes.size <= 16 * 1024 * 1024 && hash(bytes) == expected) { "Startup artwork verification failed: " + name }
                dest.writeBytes(bytes)
            }
            check(hash(dest.readBytes()) == expected) { "Startup artwork verification failed: " + name }
        }
    }
}
android.sourceSets.getByName("main").assets.srcDir(startupAssetsDir)
tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    dependsOn(prepareStartupBranding)
}
