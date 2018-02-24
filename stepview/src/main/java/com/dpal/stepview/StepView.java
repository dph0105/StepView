package com.dpal.stepview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dpal
 * @date 2018/2/23
 */

public class StepView extends View {


    private int count;//步骤点的个数
    private int normalPoint;//普通状态的图
    private int ongoingPoint;//正在进行的步骤的图
    private int completedPoint;//已经完成的图
    private int lineWidth;//线条宽度
    private int lineNormalColor;//普通的线条颜色
    private int lineCompletedColor;//已经完成的线条的颜色
    private int descTextSize;//文字字体大小
    private int descNormalTextColor;//普通的文字颜色
    private int descOngoingTextColor;//进行中的文字颜色
    private int descCompletedTextColor;//已经完成的文字颜色
    private int distanceFromText;//文字与步骤条的距离r
    private int textLocation;//文字的位置，0表示在下面或者右边，1表示在上面或者左边
    private int direction;//方向
    private List<String> desc = new ArrayList<>();//说明文字

    private Paint linePaint = new Paint();
    private Paint textPaint = new Paint();

    public StepView(Context context) {
        this(context,null);
    }

    public StepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(descTextSize);



        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StepView, defStyleAttr, 0);
        count = a.getInt(R.styleable.StepView_count, 1);
        if (count<2){
            //点数必须大于等于2，否则抛出异常
            throw new IllegalArgumentException("Step count cant be less than 2!");
        }
        normalPoint = a.getResourceId(R.styleable.StepView_normal_point, R.drawable.default_normal_point);
        ongoingPoint = a.getResourceId(R.styleable.StepView_ongoing_point, R.drawable.default_ongoing_point);
        completedPoint = a.getResourceId(R.styleable.StepView_completed_point, R.drawable.default_completed_point);
        lineWidth = a.getDimensionPixelSize(R.styleable.StepView_line_width, 16);
        lineNormalColor = a.getColor(R.styleable.StepView_line_normal_color, Color.parseColor("#151515"));
        lineCompletedColor = a.getColor(R.styleable.StepView_line_completed_color, Color.parseColor("#151515"));
        descTextSize = a.getDimensionPixelSize(R.styleable.StepView_desc_textSize, 15);
        descNormalTextColor = a.getColor(R.styleable.StepView_desc_normal_textColor, Color.BLACK);
        descOngoingTextColor = a.getColor(R.styleable.StepView_desc_ongoing_textColor, Color.BLACK);
        descCompletedTextColor = a.getColor(R.styleable.StepView_desc_completed_textColor, Color.BLACK);
        distanceFromText = a.getDimensionPixelSize(R.styleable.StepView_distance_from_text, 10);
        textLocation = a.getInt(R.styleable.StepView_text_location, 0);
        direction = a.getInt(R.styleable.StepView_step_oriention, 0);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSize = getSize(widthMeasureSpec);
        int heightSize = getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int getSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int defineSize = 0;
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，我们这里不做处理
                break;
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值
                //这时候size是父View的size

                defineSize = size;
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                defineSize = size;//这时候size是我们xml设置的值
                break;
        }
        return defineSize;
    }
}
