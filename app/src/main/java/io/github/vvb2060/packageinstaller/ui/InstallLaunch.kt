package io.github.vvb2060.packageinstaller.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vvb2060.packageinstaller.model.InstallAborted
import io.github.vvb2060.packageinstaller.model.InstallFailed
import io.github.vvb2060.packageinstaller.model.InstallInstalling
import io.github.vvb2060.packageinstaller.model.InstallParse
import io.github.vvb2060.packageinstaller.model.InstallSuccess
import io.github.vvb2060.packageinstaller.model.InstallUserAction
import io.github.vvb2060.packageinstaller.model.PackageUserAction
import io.github.vvb2060.packageinstaller.ui.theme.PackageInstallerTheme
import io.github.vvb2060.packageinstaller.viewmodel.InstallViewModel

class InstallLaunch : ComponentActivity() {
    private val installViewModel: InstallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            installViewModel.preprocessIntent(this.intent)
        }

        setContent {
            PackageInstallerTheme {
                InstallApp(viewModel = installViewModel) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun InstallApp(viewModel: InstallViewModel, onFinish: () -> Unit) {
    val installStage by viewModel.currentInstallStage.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val stage = installStage) {
            null -> {
                Text("Loading...")
            }
            is InstallAborted -> {
                if (stage.abortReason == InstallAborted.ABORT_CLOSE) {
                    onFinish()
                } else {
                    Text("Error: ${stage.abortReason}")
                }
            }
            is InstallParse -> {
                 Text("Parsing Package...")
            }
            is InstallUserAction -> {
                 Text("Confirm Install: ${stage.apkLite.label}")
            }
            is PackageUserAction -> {
                 Text("Archive Confirm")
            }
            is InstallInstalling -> {
                 Text("Installing...")
            }
            is InstallSuccess -> {
                 Text("Success!")
            }
            is InstallFailed -> {
                 Text("Failed: ${stage.statusCode}")
            }
        }
    }
}
