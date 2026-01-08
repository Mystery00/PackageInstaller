package io.github.vvb2060.packageinstaller.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.PackageUserAction

@Composable
fun ArchiveConfirmation(
    stage: PackageUserAction,
    onCancel: () -> Unit,
    onArchive: (Boolean) -> Unit, // keepData
    onToggleEnable: (Boolean) -> Unit // newEnabledState
) {
    var keepData by remember { mutableStateOf(false) }
    val info = stage.oldApk.applicationInfo!!
    val isEnabled = info.enabled
    val toggleTextRes = if (isEnabled) R.string.disable else R.string.enable

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = { onArchive(keepData) }) {
                Text(stringResource(R.string.archive))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onToggleEnable(!isEnabled) }) {
                    Text(stringResource(toggleTextRes))
                }
                TextButton(onClick = onCancel) {
                    Text(stringResource(android.R.string.cancel))
                }
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
            Column {
                Text(stringResource(R.string.archive_confirm_question))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { keepData = !keepData }
                ) {
                    Checkbox(
                        checked = keepData,
                        onCheckedChange = { keepData = it }
                    )
                    Text(stringResource(R.string.uninstall_keep_data))
                }
            }
        }
    )
}
