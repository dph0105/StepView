package com.dpal.stepview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HorizontalStepView hsv = (HorizontalStepView) findViewById(R.id.hsv);
        List<String> descs = new ArrayList<>();
        descs.add("你好好好好好好好好");
        descs.add("记得斯洛伐克");
        descs.add("但是快捷方式扩大解放螺丝钉家\n乐福就死定了空间弗兰克圣诞节快乐附近");
        hsv.setDescriptions(descs);
    }
}
