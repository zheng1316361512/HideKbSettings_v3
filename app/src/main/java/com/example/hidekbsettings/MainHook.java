package com.example.hidekbsettings;

import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "HideKbSettings";

    // 可能出现的文案变体，尽量都覆盖到
    private static final Set<String> TARGET_TEXTS = new HashSet<>();
    static {
        TARGET_TEXTS.add("键盘设置");
        TARGET_TEXTS.add("输入法设置");
        TARGET_TEXTS.add("设置输入法");
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) {
        // 重要发现：这个面板的类名是 com.miui.inputmethod.InputMethodSwitchPopupView，
        // 但它实际是被小米注入到"当前使用的输入法自己的进程"里运行的（比如搜狗、Gboard），
        // 而不是 SystemUI。所以这里不再限制包名，对所有进程都尝试 hook，
        // 具体作用域（scope）由你在 LSPosed 管理器里勾选实际用的输入法 App 来控制。

        XC_MethodHook hideIfMatch = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                try {
                    TextView tv = (TextView) param.thisObject;
                    CharSequence text = tv.getText();
                    if (text == null) return;
                    String s = text.toString().trim();
                    if (TARGET_TEXTS.contains(s)) {
                        View target = tv;
                        // 尝试往上找到整个列表项 View 一起隐藏，而不只是隐藏文字本身
                        ViewParent parent = tv.getParent();
                        int hops = 0;
                        while (parent instanceof View && hops < 3) {
                            target = (View) parent;
                            parent = parent.getParent();
                            hops++;
                        }
                        target.setVisibility(View.GONE);
                        target.setEnabled(false);
                        XposedBridge.log(TAG + ": hid view for text=" + s);
                    }
                } catch (Throwable t) {
                    XposedBridge.log(TAG + " error: " + t);
                }
            }
        };

        // TextView.setText 有多个重载，尽量都 hook 上，找不到的忽略即可
        try {
            XposedHelpers.findAndHookMethod(
                    TextView.class, "setText",
                    CharSequence.class,
                    hideIfMatch);
        } catch (Throwable ignored) {
        }

        try {
            XposedHelpers.findAndHookMethod(
                    TextView.class, "setText",
                    CharSequence.class, TextView.BufferType.class,
                    hideIfMatch);
        } catch (Throwable ignored) {
        }

        try {
            XposedHelpers.findAndHookMethod(
                    TextView.class, "setText",
                    int.class,
                    hideIfMatch);
        } catch (Throwable ignored) {
        }
    }
}
