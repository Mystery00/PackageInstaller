plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "stub"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    //noinspection GradleDependency
    compileOnly(libs.annotation)
}
