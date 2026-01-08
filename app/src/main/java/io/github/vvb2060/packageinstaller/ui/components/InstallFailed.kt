package io.github.vvb2060.packageinstaller.ui.components

import android.content.pm.PackageInstaller
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.InstallFailed

@Composable
fun InstallFailed(
    stage: InstallFailed,
    onDone: () -> Unit
) {
    val res = when (stage.statusCode) {
        PackageInstaller.STATUS_FAILURE_BLOCKED -> stringResource(R.string.install_failed_blocked)
        PackageInstaller.STATUS_FAILURE_INVALID -> stringResource(R.string.install_failed_invalid_apk)
        PackageInstaller.STATUS_FAILURE_CONFLICT -> stringResource(R.string.install_failed_conflict)
        PackageInstaller.STATUS_FAILURE_STORAGE -> stringResource(R.string.install_failed_storage)
        PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> stringResource(R.string.install_failed_incompatible)
        else -> stringResource(R.string.install_failed)
    }

    val message = buildString {
        append(res)
        append(" (").append(stage.legacyCode).append(")")
        stage.message?.let {
            appendLine()
            appendLine()
            append(it)
        }
    }

    InstallDialog(
        icon = stage.apkLite.icon,
        title = stage.apkLite.label ?: "",
        onDismiss = onDone,
        confirmButton = {
            TextButton(onClick = onDone) {
                Text(stringResource(R.string.done))
            }
        }
    ) {
        Text(message)
    }
}
