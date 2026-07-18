# HideKbSettings — LSPosed 模块

功能：隐藏输入法切换面板（长按空格/点击输入法切换按钮弹出的面板）中的
“键盘设置”/“输入法设置”这一项，避免误触。

## 编译方法

1. 用 Android Studio 打开本工程根目录（包含 settings.gradle 的这一层）。
2. 等待 Gradle Sync 完成（需要能访问 google() / mavenCentral() 以及
   https://api.xposed.info/ 这个仓库，用于下载 Xposed API）。
3. 菜单 Build → Build Bundle(s) / APK(s) → Build APK(s)。
4. 生成的 APK 在 `app/build/outputs/apk/debug/app-debug.apk`。

命令行方式（如果安装了 Android SDK + Gradle）：

```
./gradlew assembleDebug
```

## 安装与启用（重要：作用域和之前想的不一样）

经过实测排查（反编译 + 开源项目 HyperCeiler 源码比对）确认：
这个"键盘设置"按钮所在的面板，类名是
com.miui.inputmethod.InputMethodSwitchPopupView，但它实际运行在
你当前正在用的输入法自己的进程里（比如搜狗输入法
com.sohu.inputmethod.sogou、Gboard com.google.android.inputmethod.latin），
不是 SystemUI，也不是系统框架。

1. 把生成的 APK 安装到手机上（需要已 root + 已安装 LSPosed 框架）。
2. 打开 LSPosed 管理器 App → 模块 → 找到"隐藏键盘设置项"并启用。
3. 点击进入该模块的作用域(Scope)设置，这次要勾选：
   - 你实际在用的输入法 App（比如"搜狗输入法"）
   - 如果你会切换用 Gboard，也一并勾选 Gboard
   - 不需要再勾 SystemUI / 系统框架了
4. 重启手机（或者在 LSPosed 里重启作用域内的 App 即可，不一定要整机重启）。
5. 打开输入法切换面板，验证"键盘设置"这一项是否已隐藏。

## 如果没生效

说明面板里的文字控件不是走标准 TextView.setText 显示的（可能是
自绘 View 或用了 Jetpack Compose）。这种情况需要抓取具体机型的
系统界面 apk 反编译分析，再针对性修改 Hook 逻辑，不是通用方案能
覆盖的。可以把 `/system` 分区里 SystemUI 相关的 apk pull 出来，
或者用 LSPosed 日志（Xposed 显示 TAG: HideKbSettings 的 log）
辅助定位具体的类。
