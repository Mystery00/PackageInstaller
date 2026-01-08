package io.github.vvb2060.packageinstaller.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.Hook
import io.github.vvb2060.packageinstaller.model.InstallAborted
import io.github.vvb2060.packageinstaller.model.InstallFailed
import io.github.vvb2060.packageinstaller.model.InstallParse
import io.github.vvb2060.packageinstaller.model.InstallSuccess
import io.github.vvb2060.packageinstaller.model.InstallUserAction
import io.github.vvb2060.packageinstaller.model.Installing
import io.github.vvb2060.packageinstaller.model.PackageUserAction
import io.github.vvb2060.packageinstaller.ui.components.ArchiveConfirmation
import io.github.vvb2060.packageinstaller.ui.components.InstallConfirm
import io.github.vvb2060.packageinstaller.ui.components.InstallError
import io.github.vvb2060.packageinstaller.ui.components.InstallFailed
import io.github.vvb2060.packageinstaller.ui.components.InstallParse
import io.github.vvb2060.packageinstaller.ui.components.InstallSuccess
import io.github.vvb2060.packageinstaller.ui.components.Installing
import io.github.vvb2060.packageinstaller.ui.theme.PackageInstallerTheme
import io.github.vvb2060.packageinstaller.viewmodel.InstallViewModel
import rikka.shizuku.Shizuku

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

private fun checkShizuku(context: Context, intent: Intent?) {
    if (Shizuku.pingBinder()) {
        Shizuku.requestPermission(1)
        return
    }
    var finalIntent = intent
    if (finalIntent == null) {
        val web = context.getString(R.string.shizuku_url).toUri()
        finalIntent = Intent(Intent.ACTION_VIEW, web)
    }
    context.startActivity(finalIntent)
}

@Composable
fun InstallApp(viewModel: InstallViewModel, onFinish: () -> Unit) {
    val installStage by viewModel.currentInstallStage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(contentAlignment = Alignment.Center) {
        when (val stage = installStage) {
            null -> {
                // Initial loading state, could be a spinner
                Text("Loading...")
            }
            is InstallAborted -> {
                if (stage.abortReason == InstallAborted.ABORT_CLOSE) {
                    onFinish()
                } else {
                    InstallError(stage = stage) { checkShizuku ->
                        viewModel.cleanupInstall()
                        onFinish()
                        if (checkShizuku) {
                            checkShizuku(context, stage.intent)
                        }
                    }
                }
            }
            is InstallParse -> {
                InstallParse(stage = stage) {
                    viewModel.cleanupInstall()
                    onFinish()
                }
            }
            is InstallUserAction -> {
                InstallConfirm(
                    stage = stage,
                    onCancel = {
                        viewModel.cleanupInstall()
                        onFinish()
                    },
                    onInitiateInstall = { check, valid, remove ->
                        viewModel.initiateInstall(check, valid, stage.fullInstall, remove)
                    }
                )
            }
            is PackageUserAction -> {
                ArchiveConfirmation(
                    stage = stage,
                    onCancel = {
                        // On cancel just finish? Fragment says requireActivity().finish()
                        // No cleanup called in Fragment for ArchiveConfirmation?
                        // BaseDialogFragment calls cleanAndFinish on Cancel
                        // But ArchiveConfirmation setNegativeButton calls requireActivity().finish() DIRECTLY in Fragment code.
                        // Let's stick to finish.
                        onFinish()
                    },
                    onArchive = { keepData ->
                        viewModel.archivePackage(stage.oldApk, keepData)
                    },
                    onToggleEnable = { enabled ->
                        onFinish()
                        viewModel.setPackageEnabled(stage.oldApk.packageName, enabled)
                    }
                )
            }

            is Installing -> {
                val progress by viewModel.stagingProgress.collectAsStateWithLifecycle()
                Installing(
                    stage = stage,
                    progress = progress,
                    onCancel = {
                        // Cancelling during installation not trivial?
                        // Fragment says setCancelable(false) and negative button disabled.
                        // But if we want to support it, we'd need cancellation logic.
                        // For now disable or do nothing.
                        // The component has "enabled = false" for cancel button already.
                    }
                )
            }
            is InstallSuccess -> {
                InstallSuccess(
                    stage = stage,
                    onDone = {
                        viewModel.cleanupInstall()
                        onFinish()
                    },
                    onLaunch = { intent ->
                        viewModel.cleanupInstall()
                        onFinish()
                        Hook.startActivity(intent)
                    }
                )
            }
            is InstallFailed -> {
                InstallFailed(stage = stage) {
                    viewModel.cleanupInstall()
                    onFinish()
                }
            }
        }
    }
}
