## 代码质量与开发原则

### 质量要求

-   **语言规范**: 严格遵循 Kotlin 官方编码规范。
-   **Android 规范**:
    -   在适用的情况下遵循 Modern Android Development (MAD) 指南 (本项目主要使用 Views/Fragments)。
    -   **架构**: MVVM (ViewModel + Repository)。
    -   **权限**: 使用 **Shizuku** 进行高权限操作 (安装应用、绕过限制)。
    -   **兼容性**: TargetSDK 35 (Android 15)，支持应用归档 (Archiving) 等最新 Android 版本特性。
    -   **旧版支持**: 使用 `hiddenapibypass` 访问深度系统集成所需的受限 API。
-   **功能特性规范**:
    -   **应用安装**: 支持普通 APK、Split APKs 以及 ZIP 包内的 APK 安装。
    -   **Shizuku 集成**: 检查 Shizuku 权限 (`preCheck`)，并使用它伪装成 `com.android.shell` 或 `com.android.vending` 创建 `PackageInstaller` 会话。
    -   **文件处理**: 支持 `content://`, `package://`, 和 `file://` URIs。
    -   **归档功能**: 包含归档应用 (卸载但保留数据) 或导出为 ZIP 的逻辑。
    -   **UI 呈现**:
        -   基于 Fragment 的 Dialog 展示各个安装阶段 (`Parse` 解析, `Confirm` 确认, `Installing` 安装中, `Success` 成功, `Failed` 失败)。
        -   `InstallLaunch` 作为 Dialog 风格的 Activity 处理主流程。

### 代码结构

-   `model/`: 核心业务逻辑。
    -   `InstallRepository.kt`: 管理 `PackageInstaller` 会话、提交以及处理安装流程。
    -   `ApkLite.kt`: 轻量级 APK 解析逻辑。
    -   `Hook.kt`: 反射与 Hidden API 绕过机制。
-   `ui/`: 用户界面。
    -   `fragments/`: 安装步骤的独立页面 (错误、解析、确认等)。
    -   `InstallLaunch.kt`: 主入口点与 Fragment 编排。
-   `viewmodel/`:
    -   `InstallViewModel.kt`: 绑定 UI 与 `InstallRepository`，通过 `ArchTaskExecutor` 处理线程。

### 测试与验证

-   **编译检查**:
    -   确保 `./gradlew :app:assembleDebug` 编译通过。
-   **功能测试**:
    -   测试普通 APK 安装。
    -   测试 Split APK 安装 (例如来自 bundletool 的包)。
    -   测试 Shizuku 权限流程 (授权/拒绝)。
    -   在支持的设备上测试归档 (Archiving) 能力。

## 文档与记忆

文档与记忆采用 Markdown 格式，存放于 `.agentdocs/` 及其子目录下。
索引文档：`.agentdocs/index.md`

### 全局重要记忆

-   **项目名称**: PackageInstaller (Shizuku Package Installer)
-   **包名**: `io.github.vvb2060.packageinstaller`
-   **关键技术**:
    -   **Shizuku**: `INSTALL_PACKAGES` 权限与 `uid` 伪装的关键。
    -   **HiddenApiBypass**: 访问内部 PMS/Install APIs 所需。
    -   **Commons Compress / XZ**: 用于处理复杂的归档文件。
-   **SDK 版本**:
    -   Target SDK: 35.
    -   Java Version: 21.

## 任务处理指南

-   **需求澄清**: 了解用户是否需要添加新的安装源、特定 Android 版本的逻辑或 UI 变更。
-   **系统限制**: 始终考虑 `PackageInstaller` 的限制与 Shizuku 的可用性。
-   **风险记录**: 记录可能破坏 `Hook` 机制的通用 Android API 变更。

### 任务回顾

-   在完成任务前:
    -   确认 Shizuku 逻辑完整。
    -   验证 Split APK 处理无回退。
    -   检查 `libs.versions.toml` (如果存在) 或 `build.gradle.kts` 确保依赖一致性。

## 沟通原则

-   **使用中文沟通** (遵循之前的上下文/规则偏好)。
-   在适当的地方保留技术术语的英文原文 (如 Shizuku, Session, Split APK, Binder)。