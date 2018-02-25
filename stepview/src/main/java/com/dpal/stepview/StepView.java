package com.dpal.stepview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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

    public static final int HORIZONTAL = 0;//水平方向
    public static final int VERTICAL = 1;//垂直方向

    /**
     * 步骤的数量
     */
    private int count;//步骤点的个数
    /**
     * 普通的步骤点的图标
     */
    private int normalPointResId;
    private Bitmap normalBitmap;

    /**
     * 正在进行中的步骤点的图标
     */
    private int ongoingPointResId;
    private Bitmap ongoingBitmap;

    /**
     * 已经完成的步骤点的图标
     */
    private int completedPointResId;
    private Bitmap completedBitmap;
    /**
     * 步骤条的线条宽度
     */
    private int lineWidth;
    /**
     * 普通的默认的线条的颜色
     */
    private int lineNormalColor;
    /**
     * 已经完成的步骤的线条的颜色
     */
    private int lineCompletedColor;
    /**
     * 说明文字的字体大小
     */
    private int descTextSize;//文字字体大小
    /**
     * 普通的默认的文字的颜色
     */
    private int descNormalTextColor;
    /**
     * 正在进行中的步骤的文字颜色
     */
    private int descOngoingTextColor;
    /**
     * 已经完成的步骤的文字颜色
     */
    private int descCompletedTextColor;
    /**
     * 说明文字与步骤条之间的距离
     */
    private int distanceFromText;
    /**
     * 说明文字相对于步骤条的位置，如果是水平方向时，0表示在下面，1表示在上面；
     * 如果是垂直方向时，0表示在右边，1表示在左边
     */
    private int textLocation;
    /**
     * 步骤条的方向
     */
    private int direction;
    /**
     * 每个步骤的说明
     */
    private List<String> descs = new ArrayList<>();
    /**
     * 说明的宽度
     */
    private int descWidth;

    /**
     * 线条的Paint
     */
    private Paint linePaint = new Paint();
    /**
     * 文字的画笔
     */
    private TextPaint textPaint = new TextPaint();
//    private Paint textPaint = new Paint();


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
        normalPointResId = a.getResourceId(R.styleable.StepView_normal_point, R.drawable.default_normal_point);
        ongoingPointResId = a.getResourceId(R.styleable.StepView_ongoing_point, R.drawable.default_ongoing_point);
        completedPointResId = a.getResourceId(R.styleable.StepView_completed_point, R.drawable.default_completed_point);
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

        normalBitmap = BitmapFactory.decodeResource(getResources(), normalPointResId);
        ongoingBitmap = BitmapFactory.decodeResource(getResources(), ongoingPointResId);
        completedBitmap = BitmapFactory.decodeResource(getResources(), completedPointResId);
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
        int trueSize = 0;
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，我们这里不做处理
                break;
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                if (direction==VERTICAL){
                    //如果方向是垂直方向
                    //获取步骤条的宽度，为三个类型的步骤点图片与线宽的最大值
                    int barWidth = getMax(normalBitmap.getWidth(), ongoingBitmap.getWidth(), completedBitmap.getWidth(),lineWidth);
                }else if (direction==HORIZONTAL){
                    //如果方向是水平方向
                    //获取步骤条的高度，为三个类型的步骤点图片与线宽的最大值
                    int barHeight = getMax(normalBitmap.getHeight(), ongoingBitmap.getHeight(), completedBitmap.getHeight(),lineWidth);
                    if (descs.size()!=0){
                        trueSize = barHeight+getPaddingTop()+getPaddingBottom();
                    }else {
                        trueSize = barHeight+getPaddingTop()+getPaddingBottom();
                    }
                }

                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                trueSize = size;//这时候size是我们xml设置的值
                break;
        }
        return trueSize;
    }

    private int measureDescText(){
        return 0;
    }


    //得到多个int值中的最大值
    private int getMax(int ...sizes){
        int temp = 0;
        for (int size : sizes){
            if (size>temp){
                temp = size;
            }
        }
        return temp;
    }

//    private int getMultiTextMaxHeight(List<String> descs){
//        int temp = 0;
//        StaticLayout staticLayout = new StaticLayout(descs.get(0),textPaint,600, Layout.Alignment.ALIGN_NORMAL,1,0,true);
//        staticLayout.getHeight()
//    }
}
