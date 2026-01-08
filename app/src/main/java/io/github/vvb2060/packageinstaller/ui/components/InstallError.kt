package io.github.vvb2060.packageinstaller.ui.components

import android.content.pm.PackageManager
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.InstallAborted
import rikka.shizuku.Shizuku

@Composable
fun InstallError(
    stage: InstallAborted,
    onOk: (Boolean) -> Unit,
) {
    val code = stage.abortReason

    val title = if (code == InstallAborted.ABORT_INFO) stringResource(R.string.app_name)
    else stringResource(R.string.error_title)

    val message = when (code) {
        InstallAborted.ABORT_SHIZUKU -> {
            if (!Shizuku.pingBinder()) {
                if (stage.intent == null) {
                    stringResource(R.string.error_shizuku_notfound)
                } else {
                    stringResource(R.string.error_shizuku_notrunning)
                }
            } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                stringResource(R.string.error_shizuku_notpermitted)
            } else {
                stringResource(R.string.error_shizuku_unavailable)
            }
        }

        InstallAborted.ABORT_INFO -> {
            stringResource(
                R.string.error_info,
                stringResource(R.string.license)
            ) + "\n\n" + stringResource(R.string.copyright)
        }

        InstallAborted.ABORT_PARSE -> stringResource(R.string.error_parse)
        InstallAborted.ABORT_SPLIT -> stringResource(R.string.error_split)
        InstallAborted.ABORT_NOTFOUND -> stringResource(R.string.error_notfound)
        InstallAborted.ABORT_CREATE -> stringResource(R.string.error_create)
        InstallAborted.ABORT_WRITE -> stringResource(R.string.error_write)
        else -> stringResource(R.string.error_title)
    }

    InstallDialog(
        icon = R.drawable.ic_app_icon,
        title = title,
        onDismiss = { onOk(false) },
        confirmButton = {
            TextButton(onClick = {
                onOk(code == InstallAborted.ABORT_SHIZUKU)
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
    ) {
        Text(message)
    }
}
