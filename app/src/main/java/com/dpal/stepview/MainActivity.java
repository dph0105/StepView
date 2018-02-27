package com.dpal.stepview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HorizontalStepView hsv = (HorizontalStepView) findViewById(R.id.hsv);
        List<String> descs = new ArrayList<>();
        descs.add("已完成");
        descs.add("进行中");
        descs.add("未开始");
        descs.add("未开始");
        hsv.setDescriptions(descs);

    }
}
