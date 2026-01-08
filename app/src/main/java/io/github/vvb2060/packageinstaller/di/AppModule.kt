package io.github.vvb2060.packageinstaller.di

import io.github.vvb2060.packageinstaller.model.InstallRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { InstallRepository(androidContext()) }
}
