package com.dpal.stepview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dpal
 * @date 2018/2/23
 */

public class HorizontalStepView extends View {

    public static final int HORIZONTAL = 0;//水平方向
    public static final int VERTICAL = 1;//垂直方向

    /**
     * 步骤的数量
     */
    private int count;//步骤点的个数
    /**
     * 当前进度
     */
    private int currentVal = 0;
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
    private List<String> descriptions = new ArrayList<>();

    private List<StaticLayout> staticLayouts = new ArrayList<>();

    /**
     * 每个步骤项的宽度，默认100；
     */
    private int stepWidth = 100;

    /**
     * 步骤项与步骤项之间的间隔，默认为0
     */
    private int stepInterval = 0;
    /**
     * 线条的Paint
     */
    private Paint linePaint = new Paint();
    /**
     * 文字的画笔
     */
    private TextPaint textPaint = new TextPaint();
//    private Paint textPaint = new Paint();

    /**
     * 步骤条的高度
     */
    private int barHeight;

    /**
     * view的宽度
     */
    private int widthSize;

    public HorizontalStepView(Context context) {
        this(context,null);
    }

    public HorizontalStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        linePaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);




        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalStepView, defStyleAttr, 0);
        count = a.getInt(R.styleable.HorizontalStepView_count, 1);
        if (count<2){
            //点数必须大于等于2，否则抛出异常
            throw new IllegalArgumentException("Step count cant be less than 2!");
        }
        if (currentVal+1>count){
            throw new IllegalArgumentException("currentVal must be less than count!");
        }
        normalPointResId = a.getResourceId(R.styleable.HorizontalStepView_normal_point,R.drawable.icon_launcher);
        ongoingPointResId = a.getResourceId(R.styleable.HorizontalStepView_ongoing_point, R.drawable.icon_launcher);
        completedPointResId = a.getResourceId(R.styleable.HorizontalStepView_completed_point, R.drawable.icon_launcher);
        lineWidth = a.getDimensionPixelSize(R.styleable.HorizontalStepView_line_width, 16);
        lineNormalColor = a.getColor(R.styleable.HorizontalStepView_line_normal_color, Color.parseColor("#ff5566"));
        lineCompletedColor = a.getColor(R.styleable.HorizontalStepView_line_completed_color, Color.parseColor("#ff0000"));
        descTextSize = (int)a.getDimension(R.styleable.HorizontalStepView_desc_textSize, 18);
        descNormalTextColor = a.getColor(R.styleable.HorizontalStepView_desc_normal_textColor, Color.BLACK);
        descOngoingTextColor = a.getColor(R.styleable.HorizontalStepView_desc_ongoing_textColor, Color.BLACK);
        descCompletedTextColor = a.getColor(R.styleable.HorizontalStepView_desc_completed_textColor, Color.BLACK);
        distanceFromText = a.getDimensionPixelSize(R.styleable.HorizontalStepView_distance_from_text, 10);
        textLocation = a.getInt(R.styleable.HorizontalStepView_text_location, 0);
        direction = a.getInt(R.styleable.HorizontalStepView_step_oriention, 0);
        a.recycle();

        normalBitmap = BitmapFactory.decodeResource(getResources(), normalPointResId);
        ongoingBitmap = BitmapFactory.decodeResource(getResources(), ongoingPointResId);
        completedBitmap = BitmapFactory.decodeResource(getResources(), completedPointResId);
        textPaint.setTextSize(descTextSize);
        linePaint.setStrokeWidth(lineWidth);
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

        widthSize = getWidthSize(widthMeasureSpec);

        int heightSize = getHeightSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingStart();
        //画连线
        //画已完成的线

        int startX1 = left+stepWidth/2;
        int stopX1 = startX1+stepWidth*currentVal+stepInterval*currentVal;
        int startY = barHeight/2;
        int stopY = barHeight/2;
        linePaint.setColor(lineNormalColor);
        canvas.drawLine(startX1,startY,stopX1,stopY,linePaint);

        //画未完成的线
        linePaint.setColor(lineCompletedColor);
        canvas.drawLine(stopX1,startY,widthSize-getPaddingEnd()-stepWidth/2,stopY,linePaint);

        //画步骤点
        for (int i=0;i<count;i++){
            if (i==currentVal){
                canvas.drawBitmap(ongoingBitmap,left+stepWidth/2-ongoingBitmap.getWidth()/2,getPaddingTop(),linePaint);
            }else if (i<currentVal){
                canvas.drawBitmap(completedBitmap,left+stepWidth/2-completedBitmap.getWidth()/2,getPaddingTop(),linePaint);
            }else if (i>currentVal){
                canvas.drawBitmap(completedBitmap,left+stepWidth/2-completedBitmap.getWidth()/2,getPaddingTop(),linePaint);
            }
            left = left + stepWidth + stepInterval;

        }

        //画文字
        canvas.save();
        canvas.translate(0,barHeight+distanceFromText);
        for(int i=0;i<staticLayouts.size();i++){
            float dx = getPaddingStart()+stepWidth*i+stepInterval*i;

            staticLayouts.get(i).draw(canvas);
            canvas.translate(stepWidth+stepInterval,0);
        }
        canvas.restore();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int getWidthSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，我们这里不做处理
                break;
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                //获取View的宽度
                trueSize = count*stepWidth+(count-1)*stepInterval+getPaddingStart()+getPaddingEnd();
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                stepWidth = size/count;//这时候每个步骤项的宽度，为总宽度除以步骤的数量
                trueSize = size;//这时候size是我们xml设置的值
                break;
        }
        return trueSize;
    }

    private int getHeightSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，我们这里不做处理
                break;
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                //获取步骤条的高度，为三个类型的步骤点图片与线宽的最大值
                barHeight = getMax(normalBitmap.getHeight(), ongoingBitmap.getHeight(), completedBitmap.getHeight(), lineWidth);
                if (descriptions.size() != 0) {
                    //如果有说明文字，那么高度的大小就要加上说明文字中的最大高度
                    trueSize = barHeight + getMultiTextMaxHeight() + getPaddingTop() + getPaddingBottom() + distanceFromText;
                } else {
                    trueSize = barHeight + getPaddingTop() + getPaddingBottom() + distanceFromText;
                }
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                trueSize = size;//这时候size是我们xml设置的值
                break;
        }
        return trueSize;
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

    //获得多个Text最大的高度
    private int getMultiTextMaxHeight(){
        int temp = 0;
        staticLayouts.clear();
        for (int i=0;i<descriptions.size();i++){
            StaticLayout staticLayout = new StaticLayout(descriptions.get(i),textPaint,stepWidth, Layout.Alignment.ALIGN_CENTER,1,0,true);
            staticLayouts.add(staticLayout);
            if (staticLayout.getHeight()>temp){
                temp = staticLayout.getHeight();
            }
        }
        return temp;
    }

    public void setDescriptions(List<String> descs){
        descriptions.clear();
        descriptions.addAll(descs);
        invalidate();
        requestLayout();
    }

    public void setDescriptions(String[] descs){
        descriptions.clear();
        setDescriptions(Arrays.asList(descs));
    }
}
