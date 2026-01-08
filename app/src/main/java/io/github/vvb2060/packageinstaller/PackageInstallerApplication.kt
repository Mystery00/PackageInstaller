package io.github.vvb2060.packageinstaller

import android.app.Application
import io.github.vvb2060.packageinstaller.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PackageInstallerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PackageInstallerApplication)
            modules(appModule)
        }
    }
}
