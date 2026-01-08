package io.github.vvb2060.packageinstaller.ui.components

import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.vvb2060.packageinstaller.BuildConfig
import io.github.vvb2060.packageinstaller.R
import io.github.vvb2060.packageinstaller.model.InstallUserAction

@Composable
fun InstallConfirm(
    stage: InstallUserAction,
    onCancel: () -> Unit,
    onInitiateInstall: (check: Boolean, valid: Boolean, remove: Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    val installer = stage.oldApk?.sharedUserId == BuildConfig.APPLICATION_ID
    var installed = false
    stage.oldApk?.applicationInfo?.let {
        installed = (it.flags and ApplicationInfo.FLAG_INSTALLED) != 0
    }

    LaunchedEffect(stage) {
        if (installer) {
            checked = true
        }
    }

    var question = stringResource(R.string.install_confirm_question)
    if (installed) {
        question = stringResource(R.string.install_confirm_question_update)
    }

    val full = stage.fullInstall
    var removeSplit = false
    if (!full && stage.oldApk?.splitNames != null) {
        question = stringResource(R.string.install_confirm_question_split)
        for (splitName in stage.oldApk.splitNames) {
            if (splitName == stage.apkLite.splitName) {
                question = stringResource(R.string.install_confirm_question_split_remove)
                removeSplit = true
                break
            }
        }
    }

    InstallDialog(
        icon = stage.apkLite.icon,
        title = stage.apkLite.label ?: "",
        onDismiss = onCancel,
        confirmButton = {
            TextButton(
                onClick = { onInitiateInstall(checked, true, removeSplit) }
            ) {
                Text(stringResource(if (stage.oldApk != null) R.string.update else R.string.install))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        negativeButton = {
            TextButton(onClick = { onInitiateInstall(checked, false, removeSplit) }) {
                Text(stringResource(R.string.add_more))
            }
        }
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = question,
                fontStyle = FontStyle.Italic,
            )
            Spacer(modifier = Modifier.height(8.dp))

            InstallInfoText(stage)

            if (!stage.skipCreate) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checked = !checked }
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                    Text(stringResource(R.string.set_installer))
                }
            }
        }
    }
}

@Composable
fun InstallInfoText(stage: InstallUserAction) {
    val apk = stage.apkLite
    val old = stage.oldApk

    val infoMessage = buildAnnotatedString {
        append(stringResource(R.string.package_name))
        appendLine(apk.packageName)

        if (apk.needSplit()) {
            append(stringResource(R.string.split_name))
            appendLine(apk.splitName)
            append(stringResource(R.string.split_types))
            appendLine(apk.splitTypes)
            append(stringResource(R.string.required_split_types))
            appendLine(apk.requiredSplitTypes)
        }
        if (!apk.isSplit()) {
            val ver = "${apk.versionName} (${apk.versionCode})"
            val min = getAndroidName(apk.minSdkVersion)
            val target = getAndroidName(apk.targetSdkVersion)

            if (old != null) {
                val oldInfo = old.applicationInfo!!
                val oldVer = "${old.versionName} (${old.longVersionCode})"
                val oldMin = getAndroidName(oldInfo.minSdkVersion)
                val oldTarget = getAndroidName(oldInfo.targetSdkVersion)

                fun appendVersionChange(
                    oldVer: String,
                    oldCode: Long,
                    newVer: String,
                    newCode: Long
                ) {
                    if (oldCode == newCode) {
                        appendLine(newVer)
                        return
                    }
                    append(oldVer)
                    append(" → ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(newVer)
                    }
                    appendLine(if (oldCode < newCode) " ▲" else " ▼")
                }

                fun appendChange(oldVal: String, newVal: String) {
                    if (oldVal == newVal) {
                        appendLine(newVal)
                    } else {
                        append(oldVal)
                        append(" → ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            appendLine(newVal)
                        }
                    }
                }

                append(stringResource(R.string.version))
                appendVersionChange(oldVer, old.longVersionCode, ver, apk.versionCode)
                append(stringResource(R.string.min_sdk))
                appendChange(oldMin, min)
                append(stringResource(R.string.target_sdk))
                appendChange(oldTarget, target)
            } else {
                append(stringResource(R.string.version))
                appendLine(ver)
                append(stringResource(R.string.min_sdk))
                appendLine(min)
                append(stringResource(R.string.target_sdk))
                appendLine(target)
            }
        }
    }
    Text(infoMessage)
}

fun getAndroidName(apiLevelStr: String?): String {
    if (apiLevelStr == null) return "???"
    return try {
        val api = apiLevelStr.toInt()
        getAndroidName(api)
    } catch (_: NumberFormatException) {
        apiLevelStr
    }
}

fun getAndroidName(apiLevel: Int): String {
    val name = when (apiLevel) {
        Build.VERSION_CODES.CUR_DEVELOPMENT -> "Dev"
        1 -> "1.0" // BASE
        2 -> "1.1" // BASE_1_1
        3 -> "1.5" // CUPCAKE
        4 -> "1.6" // DONUT
        5 -> "2.0" // ECLAIR
        6 -> "2.0.1" // ECLAIR_0_1
        7 -> "2.1" // ECLAIR_MR1
        8 -> "2.2" // FROYO
        9 -> "2.3" // GINGERBREAD
        10 -> "2.3.3" // GINGERBREAD_MR1
        11 -> "3.0" // HONEYCOMB
        12 -> "3.1" // HONEYCOMB_MR1
        13 -> "3.2" // HONEYCOMB_MR2
        14 -> "4.0" // ICE_CREAM_SANDWICH
        15 -> "4.0.3" // ICE_CREAM_SANDWICH_MR1
        16 -> "4.1" // JELLY_BEAN
        17 -> "4.2" // JELLY_BEAN_MR1
        18 -> "4.3" // JELLY_BEAN_MR2
        19 -> "4.4" // KITKAT
        20 -> "4.4W" // KITKAT_WATCH
        21 -> "5.0" // LOLLIPOP
        22 -> "5.1" // LOLLIPOP_MR1
        23 -> "6.0" // M
        24 -> "7.0" // N
        25 -> "7.1" // N_MR1
        26 -> "8.0" // O
        27 -> "8.1" // O_MR1
        28 -> "9" // P
        29 -> "10" // Q
        30 -> "11" // R
        31 -> "12" // S
        32 -> "12L" // S_V2
        33 -> "13" // TIRAMISU
        34 -> "14" // UPSIDE_DOWN_CAKE
        35 -> "15" // VANILLA_ICE_CREAM
        36 -> "16" // BAKLAVA
        else -> "???"
    }
    return "Android $name ($apiLevel)"
}
