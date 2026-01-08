package io.github.vvb2060.packageinstaller.ui.components

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.InstallSuccess

@Composable
fun InstallSuccess(
    stage: InstallSuccess,
    onDone: () -> Unit,
    onLaunch: (Intent) -> Unit
) {
    val message = if (stage.path != null) {
        stringResource(R.string.archive_done) + stage.path
    } else {
        stringResource(R.string.install_done)
    }

    AlertDialog(
        onDismissRequest = onDone,
        confirmButton = {
            stage.startIntent?.let { intent ->
                TextButton(onClick = { onLaunch(intent) }) {
                    Text(stringResource(R.string.launch))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDone) {
                Text(stringResource(R.string.done))
            }
        },
        icon = {
            stage.apkLite.icon?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(stage.apkLite.label ?: "")
        },
        text = {
            Text(message)
        }
    )
}
