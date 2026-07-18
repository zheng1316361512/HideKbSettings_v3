package com.example.hidekbsettings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("请在 LSPosed 管理器中启用本模块，\n并勾选作用域：\nAndroid 系统框架 / 系统界面(SystemUI) / MIUI系统界面\n然后重启手机。");
        tv.setPadding(40, 100, 40, 40);
        tv.setTextSize(16);
        setContentView(tv);
    }
}
