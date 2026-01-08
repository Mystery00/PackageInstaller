package io.github.vvb2060.packageinstaller.viewmodel

import android.content.Intent
import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vvb2060.packageinstaller.model.InstallRepository
import io.github.vvb2060.packageinstaller.model.InstallStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstallViewModel : ViewModel(), KoinComponent {
    private val repository: InstallRepository by inject()

    val currentInstallStage: StateFlow<InstallStage?>
        get() = repository.installResult
    val stagingProgress: StateFlow<Int>
        get() = repository.stagingProgress

    fun preprocessIntent(intent: Intent) {
        if (repository.preCheck(intent)) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.parseUri()
            }
        }
    }

    fun cleanupInstall() {
        repository.cleanupInstall()
    }

    fun initiateInstall(
        setInstaller: Boolean,
        commit: Boolean,
        full: Boolean,
        removeSplit: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.install(setInstaller, commit, full, removeSplit)
        }
    }

    fun archivePackage(info: PackageInfo, uninstall: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.archivePackage(info, uninstall)
        }
    }

    fun setPackageEnabled(packageName: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPackageEnabled(packageName, enabled)
        }
    }
}
