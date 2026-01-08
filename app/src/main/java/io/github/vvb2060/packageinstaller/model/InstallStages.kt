package io.github.vvb2060.packageinstaller.model

import android.content.Intent
import android.content.pm.PackageInfo
import androidx.compose.runtime.Immutable

sealed class InstallStage()

@Immutable
class InstallParse : InstallStage()

@Immutable
class InstallUserAction(
    val apkLite: ApkLite,
    val oldApk: PackageInfo?,
    val fullInstall: Boolean = true,
    val skipCreate: Boolean = true,
) : InstallStage()

@Immutable
class PackageUserAction(
    val apkLite: ApkLite,
    val oldApk: PackageInfo,
) : InstallStage()

@Immutable
class Installing(
    val apkLite: ApkLite,
) : InstallStage()

@Immutable
class InstallSuccess(
    val apkLite: ApkLite,
    val startIntent: Intent?,
    val path: String? = null,
) : InstallStage()

@Immutable
class InstallFailed(
    val apkLite: ApkLite,
    val legacyCode: Int,
    val statusCode: Int,
    val message: String?,
) : InstallStage()

@Immutable
class InstallAborted(
    val abortReason: Int,
    val intent: Intent? = null,
) : InstallStage() {

    companion object {
        const val ABORT_CLOSE = 0
        const val ABORT_SHIZUKU = 1
        const val ABORT_PARSE = 2
        const val ABORT_SPLIT = 3
        const val ABORT_NOTFOUND = 4
        const val ABORT_CREATE = 5
        const val ABORT_WRITE = 6
        const val ABORT_INFO = 7
    }
}
