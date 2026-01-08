package io.github.vvb2060.packageinstaller.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.Installing

@Composable
fun Installing(
    stage: Installing,
    progress: Int,
    onCancel: () -> Unit
) {
    InstallDialog(
        icon = stage.apkLite.icon,
        title = stage.apkLite.label ?: "",
        dismissButton = {
            TextButton(onClick = onCancel, enabled = false) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val isIndeterminate = progress < 0 || progress > 100
            val statusText = if (progress <= 100) stringResource(R.string.copying)
            else stringResource(R.string.installing)

            Text(text = statusText)
            Spacer(modifier = Modifier.height(16.dp))

            if (isIndeterminate) {
                LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearWavyProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
