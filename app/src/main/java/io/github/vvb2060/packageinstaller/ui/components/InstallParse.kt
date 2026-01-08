package io.github.vvb2060.packageinstaller.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.InstallParse

@Composable
fun InstallParse(
    stage: InstallParse,
    onCancel: () -> Unit
) {
    InstallDialog(
        icon = R.drawable.ic_app_icon,
        title = stringResource(R.string.app_name),
        dismissButton = {
            TextButton(onClick = onCancel, enabled = false) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoadingIndicator()
            Spacer(modifier = Modifier.width(16.dp))
            Text(stringResource(R.string.parsing))
        }
    }
}
